package com.taken_seat.booking_service.booking.infrastructure.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.dto.mapper.BookingMapper;
import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.application.service.BookingProducer;
import com.taken_seat.booking_service.booking.application.service.RedissonService;
import com.taken_seat.booking_service.booking.domain.BenefitUsageHistory;
import com.taken_seat.booking_service.booking.domain.BookingCommand;
import com.taken_seat.booking_service.booking.domain.BookingStatus;
import com.taken_seat.booking_service.booking.domain.repository.BenefitUsageHistoryRepository;
import com.taken_seat.booking_service.booking.domain.repository.BookingCommandRepository;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.SeatLayoutResponseDto;
import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.booking_service.common.service.RedisService;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.QueueEnterMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingCommandService {

	private final BookingClientService bookingClientService;
	private final BookingProducer bookingProducer;
	private final BookingCommandRepository bookingCommandRepository;
	private final BenefitUsageHistoryRepository benefitUsageHistoryRepository;
	private final BookingMapper bookingMapper;
	private final RedisService redisService;
	private final RedissonService redissonService;

	public BookingCreateResponse createBooking(BookingCreateCommand command) {

		log.info("[BookingCommand] 예약 생성 - 시도: | userId={}", command.userId());

		// 중복 체크
		if (bookingCommandRepository.isUniqueBooking(command.userId(), command.performanceId(),
			command.performanceScheduleId(), command.scheduleSeatId())) {

			log.warn(
				"[BookingCommand] 예약 생성 - 실패: {} | userId={}",
				ResponseCode.BOOKING_DUPLICATED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_DUPLICATED_EXCEPTION);
		}

		// 좌석 선점
		BookingSeatClientResponseDto responseDto = redissonService.tryHoldSeat(
			command.performanceId(),
			command.performanceScheduleId(),
			command.scheduleSeatId()
		);

		BookingCommand bookingCommand = BookingCommand.builder()
			.userId(command.userId())
			.performanceId(command.performanceId())
			.performanceScheduleId(command.performanceScheduleId())
			.scheduleSeatId(command.scheduleSeatId())
			.price(responseDto.price())
			.discountedPrice(responseDto.price())
			.build();
		bookingCommand.prePersist(command.userId());

		BookingCommand saved = bookingCommandRepository.save(bookingCommand);

		// Query 전송
		bookingProducer.sendBookingCreatedEvent(bookingMapper.toEvent(bookingCommand));

		// 예매 만료 대기시간 설정
		redisService.setBookingExpire(saved.getId());

		// 대기열 입장 메시지 전송
		QueueEnterMessage queueEnterMessage = QueueEnterMessage.builder()
			.performanceId(command.performanceId())
			.performanceScheduleId(command.performanceScheduleId())
			.build();
		bookingProducer.sendQueueEnterResponse(queueEnterMessage);

		log.info("[BookingCommand] 예약 생성 - 성공: | userId={}", command.userId());

		return BookingCreateResponse.toDto(saved);
	}

	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "readBooking", key = "#command.bookingId()"),
		@CacheEvict(value = "adminReadBooking", key = "#command.bookingId()")
	})
	public void cancelBooking(BookingSingleTargetCommand command) {

		log.info("[BookingCommand] 예약 취소 - 시도: | userId={}", command.userId());

		BookingCommand bookingCommand = findBookingByIdAndUserId(command.bookingId(), command.userId());
		BookingStatus status = bookingCommand.getBookingStatus();

		LocalDateTime startAt = bookingClientService.getPerformanceStartTime(bookingCommand.getPerformanceId(),
			bookingCommand.getPerformanceScheduleId()).startAt();
		LocalDateTime now = LocalDateTime.now();

		// 현재 시각이 공연 시작 하루 전 앞인지 확인
		if (now.isAfter(startAt.minusDays(1))) {
			log.warn(
				"[BookingCommand] 예약 취소 - 실패: {} | userId={}",
				ResponseCode.BOOKING_CANCEL_NOT_ALLOWED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_CANCEL_NOT_ALLOWED_EXCEPTION);
		}

		if (status == BookingStatus.CANCELED) {
			log.warn(
				"[BookingCommand] 예약 취소 - 실패: {} | userId={}",
				ResponseCode.BOOKING_ALREADY_CANCELED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_ALREADY_CANCELED_EXCEPTION);
		} else if (status == BookingStatus.COMPLETED) {
			// 환불 요청 전송
			PaymentRefundMessage message = PaymentRefundMessage.builder()
				.bookingId(bookingCommand.getId())
				.paymentId(bookingCommand.getPaymentId())
				.userId(bookingCommand.getUserId())
				.price(bookingCommand.getDiscountedPrice())
				.type(PaymentRefundMessage.MessageType.REQUEST)
				.build();

			bookingProducer.sendPaymentRefundRequest(message);

			log.info("[BookingCommand] 예약 취소 - 환불 요청 전송: | userId={}", command.userId());
		}

		// 좌석 선점 해제 요청 보내기
		BookingSeatClientRequestDto dto = new BookingSeatClientRequestDto(
			bookingCommand.getPerformanceId(),
			bookingCommand.getPerformanceScheduleId(),
			bookingCommand.getScheduleSeatId()
		);
		BookingSeatClientResponseDto responseDto = bookingClientService.cancelSeatStatus(dto);

		if (responseDto.reserved()) {
			log.warn(
				"[BookingCommand] 예약 취소 - 실패: {} | userId={}",
				ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION);
		}

		bookingCommand.cancel(command.userId());

		// Query 전송
		bookingProducer.sendBookingUpdatedEvent(bookingMapper.toEvent(bookingCommand));

		log.info("[BookingCommand] 예약 취소 - 성공: | userId={}", command.userId());
	}

	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "readBooking", key = "#command.bookingId()"),
		@CacheEvict(value = "adminReadBooking", key = "#command.bookingId()")
	})
	public void deleteBooking(BookingSingleTargetCommand command) {

		log.info("[BookingCommand] 예약 삭제 - 시도: | userId={}", command.userId());

		BookingCommand bookingCommand = findBookingByIdAndUserId(command.bookingId(), command.userId());
		BookingStatus status = bookingCommand.getBookingStatus();

		if (status == BookingStatus.PENDING) {
			log.warn(
				"[BookingCommand] 예약 삭제 - 실패: {} | userId={}",
				ResponseCode.BOOKING_DELETE_NOT_ALLOWED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_DELETE_NOT_ALLOWED_EXCEPTION);
		}

		bookingCommand.delete(command.userId());

		// Query 전송
		bookingProducer.sendBookingUpdatedEvent(bookingMapper.toEvent(bookingCommand));

		log.info("[BookingCommand] 예약 삭제 - 성공: | userId={}", command.userId());
	}

	public void createPayment(BookingPaymentCommand command) {

		log.info("[BookingCommand] 예매 결제 - 시도: | userId={}, bookingId={}", command.userId(), command.bookingId());

		BookingCommand bookingCommand = findBookingByIdAndUserId(command.bookingId(), command.userId());

		boolean isUsedCoupon = command.couponId() != null;
		boolean isUsedMileage = command.mileage() != null && command.mileage() > 0;

		// 마일리지나 쿠폰을 사용한 경우 -> 비동기 차감 요청 이벤트 전송
		if (isUsedCoupon || isUsedMileage) {
			UserBenefitMessage benefitUsageRequestMessage = UserBenefitMessage.builder()
				.bookingId(command.bookingId())
				.userId(command.userId())
				.couponId(command.couponId())
				.mileage(command.mileage())
				.price(bookingCommand.getPrice())
				.build();

			bookingProducer.sendBenefitUsageRequest(benefitUsageRequestMessage);
			log.info(
				"[BookingCommand] 예매 결제 - 쿠폰, 마일리지 사용 요청 전송: | userId={}, bookingId={}",
				command.userId(),
				command.bookingId()
			);
			return;
		}

		// 마일리지, 쿠폰을 사용하지 않은 경우 바로 결제 요청
		PaymentMessage message = PaymentMessage.builder()
			.bookingId(command.bookingId())
			.userId(command.userId())
			.price(bookingCommand.getPrice())
			.type(PaymentMessage.MessageType.REQUEST)
			.build();

		bookingProducer.sendPaymentRequest(message);

		log.info(
			"[BookingCommand] 예매 결제 - 쿠폰, 마일리지 사용 없이 결제 요청 전송: | userId={}, bookingId={}",
			command.userId(),
			command.bookingId()
		);
	}

	@Transactional
	public void receiveBookingExpireEvent(UUID bookingId) {

		log.info("[BookingCommand] 예매 만기 - 시도: | bookingId={}", bookingId);

		BookingCommand bookingCommand = bookingCommandRepository.findById(bookingId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
		BookingStatus status = bookingCommand.getBookingStatus();
		UUID system = UUID.fromString("00000000-0000-0000-0000-000000000000");

		if (status == BookingStatus.PENDING) {
			bookingCommand.cancel(system);
			bookingCommand.delete(system);

			// Query 수정
			bookingProducer.sendBookingUpdatedEvent(bookingMapper.toEvent(bookingCommand));

			BookingSeatClientRequestDto dto = new BookingSeatClientRequestDto(
				bookingCommand.getPerformanceId(),
				bookingCommand.getPerformanceScheduleId(),
				bookingCommand.getScheduleSeatId()
			);
			BookingSeatClientResponseDto responseDto = bookingClientService.cancelSeatStatus(dto);

			if (responseDto.reserved()) {
				log.warn(
					"[BookingCommand] 예매 만기 - 실패: {} | bookingId={}",
					ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION.getMessage(),
					bookingId
				);
				throw new BookingException(ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION);
			}

			log.info("[BookingCommand] 예매 만기 - 성공: | bookingId={}", bookingId);
		}
	}

	@Transactional
	public void receiveBenefitUsageMessage(UserBenefitMessage message) {

		log.info(
			"[BookingCommand] 예매 쿠폰, 마일리지 적용 메시지 수신 - 시도: | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
		);

		BookingCommand bookingCommand = bookingCommandRepository.findById(message.getBookingId())
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
		int price = bookingCommand.getPrice(); // 정가

		if (message.getStatus() == UserBenefitMessage.UserBenefitStatus.SUCCESS) {
			if (message.getDiscount() != null) {
				double discountAmount = price * (message.getDiscount() / 100.0);  // 할인 금액 계산
				price = (int)(price - discountAmount); // 할인된 가격 계산

				if (!isValidPrice(price)) {
					log.warn(
						"[BookingCommand] 예매 쿠폰, 마일리지 적용 메시지 수신 - 실패: {} | userId={}, bookingId={}",
						ResponseCode.INVALID_COUPON.getMessage(),
						message.getUserId(),
						message.getBookingId()
					);
					throw new BookingException(ResponseCode.INVALID_COUPON);
				}
			}

			// 마일리지 차감
			if (message.getMileage() != null) {
				price -= message.getMileage();

				if (!isValidPrice(price)) {
					log.warn(
						"[BookingCommand] 예매 쿠폰, 마일리지 적용 메시지 수신 - 실패: {} | userId={}, bookingId={}",
						ResponseCode.INVALID_MILEAGE.getMessage(),
						message.getUserId(),
						message.getBookingId()
					);
					throw new BookingException(ResponseCode.INVALID_MILEAGE);
				}
			}

			// 쿠폰, 마일리지 사용내역 저장
			BenefitUsageHistory history = BenefitUsageHistory.builder()
				.bookingId(bookingCommand.getId())
				.couponId(message.getCouponId())
				.mileage(message.getMileage())
				.refunded(false)
				.build();
			history.prePersist(message.getUserId());
			benefitUsageHistoryRepository.save(history);

			// 결제 요청
			PaymentMessage paymentMessage = PaymentMessage.builder()
				.bookingId(bookingCommand.getId())
				.userId(message.getUserId())
				.price(bookingCommand.getDiscountedPrice())
				.type(PaymentMessage.MessageType.REQUEST)
				.build();
			bookingProducer.sendPaymentRequest(paymentMessage);

			log.info(
				"[BookingCommand] 예매 쿠폰, 마일리지 적용 메시지 수신 - 결제 요청 전송: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);

			bookingCommand.discount(price); // 할인가 업데이트

			// Query 전송
			bookingProducer.sendBookingUpdatedEvent(bookingMapper.toEvent(bookingCommand));

			log.info(
				"[BookingCommand] 예매 쿠폰, 마일리지 적용 메시지 수신 - 성공: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);
		} else {
			log.warn(
				"[BookingCommand] 예매 쿠폰, 마일리지 적용 메시지 수신 - 실패: {} | userId={}, bookingId={}",
				ResponseCode.BOOKING_BENEFIT_USAGE_FAILED_EXCEPTION.getMessage(),
				message.getUserId(),
				message.getBookingId()
			);
			throw new BookingException(ResponseCode.BOOKING_BENEFIT_USAGE_FAILED_EXCEPTION);
		}
	}

	@Transactional
	public void receivePaymentMessage(PaymentMessage message) {

		log.info(
			"[BookingCommand] 예매 결제 메시지 수신 - 시도: | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
		);

		BookingCommand bookingCommand = bookingCommandRepository.findById(message.getBookingId())
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
		Optional<BenefitUsageHistory> optional = benefitUsageHistoryRepository.findByBookingIdAndRefundedIsFalse(
			message.getBookingId());
		PaymentMessage.PaymentResultStatus status = message.getStatus();

		// 성공시
		if (status == PaymentMessage.PaymentResultStatus.SUCCESS) {
			bookingCommand.paymentComplete(message.getPaymentId());
			bookingCommand.preUpdate(message.getUserId());

			// Query 전송
			bookingProducer.sendBookingUpdatedEvent(bookingMapper.toEvent(bookingCommand));

			// 티켓 생성 요청
			bookingProducer.sendTicketRequest(
				TicketRequestMessage.builder()
					.userId(bookingCommand.getUserId())
					.bookingId(bookingCommand.getId())
					.performanceId(bookingCommand.getPerformanceId())
					.performanceScheduleId(bookingCommand.getPerformanceScheduleId())
					.seatId(bookingCommand.getScheduleSeatId())
					.build()
			);

			// 쿠폰, 마일리지 사용 내역 전달
			if (optional.isPresent()) {
				BenefitUsageHistory benefitUsageHistory = optional.get();
				UserBenefitMessage benefitMessage = UserBenefitMessage.builder()
					.bookingId(message.getBookingId())
					.userId(message.getUserId())
					.couponId(benefitUsageHistory.getCouponId())
					.mileage(benefitUsageHistory.getMileage())
					.price(bookingCommand.getPrice())
					.status(UserBenefitMessage.UserBenefitStatus.SUCCESS)
					.build();

				bookingProducer.sendBenefitRefundRequest(benefitMessage);
				log.info(
					"[BookingCommand] 예매 결제 메시지 수신 - 성공: 쿠폰, 마일리지 사용내역 전송 | userId={}, bookingId={}",
					message.getUserId(),
					message.getBookingId()
				);
			}

			log.info(
				"[BookingCommand] 예매 결제 메시지 수신 - 성공: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);

		} else {
			// 실패시 사용한 쿠폰, 마일리지 원복처리

			if (optional.isPresent()) {
				BenefitUsageHistory benefitUsageHistory = optional.get();
				benefitUsageHistory.refunded(message.getUserId());
			}
			log.warn(
				"[BookingCommand] 예매 결제 메시지 수신 - 실패: {} | userId={}, bookingId={}",
				ResponseCode.BOOKING_PAYMENT_FAILED_EXCEPTION.getMessage(),
				message.getUserId(),
				message.getBookingId()
			);

			throw new BookingException(ResponseCode.BOOKING_PAYMENT_FAILED_EXCEPTION);
		}
	}

	@Transactional
	public void receivePaymentRefundMessage(PaymentRefundMessage message) {

		log.info(
			"[BookingCommand] 예매 환불 메시지 수신 - 시도: | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
		);

		if (message.getStatus() == PaymentRefundMessage.PaymentRefundStatus.SUCCESS) {
			BookingCommand bookingCommand = bookingCommandRepository.findById(message.getBookingId())
				.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
			bookingCommand.cancel(message.getUserId());

			// Query 전송
			bookingProducer.sendBookingUpdatedEvent(bookingMapper.toEvent(bookingCommand));

			Optional<BenefitUsageHistory> optional = benefitUsageHistoryRepository.findByBookingIdAndRefundedIsFalse(
				bookingCommand.getId());

			// 쿠폰, 마일리지 원복 요청
			if (optional.isPresent()) {
				BenefitUsageHistory history = optional.get();

				UserBenefitMessage benefitMessage = UserBenefitMessage.builder()
					.bookingId(bookingCommand.getId())
					.userId(bookingCommand.getUserId())
					.couponId(history.getCouponId())
					.mileage(history.getMileage())
					.price(bookingCommand.getPrice())
					.status(UserBenefitMessage.UserBenefitStatus.REFUND)
					.build();
				bookingProducer.sendBenefitRefundRequest(benefitMessage);

				log.info(
					"[BookingCommand] 예매 환불 메시지 수신 - 쿠폰, 마일리지 원복 요청 전송: | userId={}, bookingId={}",
					message.getUserId(),
					message.getBookingId()
				);
			}

			// 좌석 선점 취소 요청
			BookingSeatClientRequestDto dto = new BookingSeatClientRequestDto(
				bookingCommand.getPerformanceId(),
				bookingCommand.getPerformanceScheduleId(),
				bookingCommand.getScheduleSeatId()
			);
			BookingSeatClientResponseDto responseDto = bookingClientService.cancelSeatStatus(dto);

			if (responseDto.reserved()) {
				throw new BookingException(ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION);
			}

			log.info(
				"[BookingCommand] 예매 환불 메시지 수신 - 성공: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);
		} else {
			log.warn(
				"[BookingCommand] 예매 환불 메시지 수신 - 실패: {} | userId={}, bookingId={}",
				ResponseCode.BOOKING_REFUND_FAILED_EXCEPTION.getMessage(),
				message.getUserId(),
				message.getBookingId()
			);
			throw new BookingException(ResponseCode.BOOKING_REFUND_FAILED_EXCEPTION);
		}
	}

	@Transactional
	public void receiveBenefitRefundMessage(UserBenefitMessage message) {

		log.info(
			"[BookingCommand] 예매 쿠폰, 마일리지 사용 내역 환불처리 - 시도: | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
		);

		if (message.getStatus() == UserBenefitMessage.UserBenefitStatus.SUCCESS) {
			BenefitUsageHistory history = benefitUsageHistoryRepository.findByBookingIdAndRefundedIsFalse(
					message.getBookingId())
				.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_BENEFIT_USAGE_NOT_FOUND_EXCEPTION));

			history.refunded(message.getUserId());

			log.info(
				"[BookingCommand] 예매 쿠폰, 마일리지 사용 내역 환불처리 - 성공: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);
		} else {
			log.warn(
				"[BookingCommand] 예매 쿠폰, 마일리지 사용 내역 환불처리 - 실패: {} | userId={}, bookingId={}",
				ResponseCode.BOOKING_BENEFIT_USAGE_REFUND_FAILED_EXCEPTION.getMessage(),
				message.getUserId(),
				message.getBookingId()
			);
			throw new BookingException(ResponseCode.BOOKING_BENEFIT_USAGE_REFUND_FAILED_EXCEPTION);
		}
	}

	@Transactional
	public void receiveWaitingQueueMessage(BookingRequestMessage message) {

		log.info(
			"[BookingCommand] 대기열에서 입장: | userId={}, performanceId={}, performanceScheduleId={}",
			message.getUserId(),
			message.getPerformanceId(),
			message.getPerformanceScheduleId()
		);

		SeatLayoutResponseDto layout = bookingClientService.getSeatLayout(message.getPerformanceScheduleId());
		List<UUID> seatIds = layout.seats().stream()
			.filter(e -> e.seatStatus().equals(SeatLayoutResponseDto.ScheduleSeatResponseDto.SeatStatus.AVAILABLE))
			.map(SeatLayoutResponseDto.ScheduleSeatResponseDto::scheduleSeatId)
			.toList();

		Random random = new Random();
		int i = random.nextInt(seatIds.size());
		UUID seatId = seatIds.get(i);

		BookingCreateCommand command = new BookingCreateCommand(
			message.getUserId(),
			"",
			"",
			message.getPerformanceId(),
			message.getPerformanceScheduleId(),
			seatId
		);

		createBooking(command);
	}

	private BookingCommand findBookingByIdAndUserId(UUID id, UUID userId) {
		return bookingCommandRepository.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
	}

	private boolean isValidPrice(int price) {
		return price > 0;
	}
}