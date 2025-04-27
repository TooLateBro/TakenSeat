package com.taken_seat.booking_service.booking.infrastructure.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.booking.application.dto.command.BookingAdminPageReadCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPageReadCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.application.service.BookingProducer;
import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.booking_service.booking.application.service.RedissonService;
import com.taken_seat.booking_service.booking.domain.BenefitUsageHistory;
import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.BookingStatus;
import com.taken_seat.booking_service.booking.domain.repository.BenefitUsageHistoryRepository;
import com.taken_seat.booking_service.booking.domain.repository.BookingAdminRepository;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.SeatLayoutResponseDto;
import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.booking_service.common.service.RedisService;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
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
public class BookingServiceImpl implements BookingService {

	private final BenefitUsageHistoryRepository benefitUsageHistoryRepository;
	private final BookingAdminRepository bookingAdminRepository;
	private final BookingClientService bookingClientService;
	private final BookingProducer bookingProducer;
	private final BookingRepository bookingRepository;
	private final RedisService redisService;
	private final RedissonService redissonService;

	@Override
	@Transactional
	public BookingCreateResponse createBooking(BookingCreateCommand command) {

		log.info("[Booking] 예약 생성 - 시도: | userId={}", command.userId());

		// 중복 체크
		if (bookingRepository.isUniqueBooking(command.userId(), command.performanceId(),
			command.performanceScheduleId(), command.scheduleSeatId())) {

			log.warn(
				"[Booking] 예약 생성 - 실패: {} | userId={}",
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

		Booking booking = Booking.builder()
			.userId(command.userId())
			.performanceId(command.performanceId())
			.performanceScheduleId(command.performanceScheduleId())
			.scheduleSeatId(command.scheduleSeatId())
			.price(responseDto.price())
			.discountedPrice(responseDto.price())
			.build();
		booking.prePersist(command.userId());

		Booking saved = bookingRepository.save(booking);

		// 예매 만료 설정
		redisService.setBookingExpire(saved.getId());

		// 대기열 입장 메시지 전송
		QueueEnterMessage queueEnterMessage = QueueEnterMessage.builder()
			.performanceId(command.performanceId())
			.performanceScheduleId(command.performanceScheduleId())
			.build();
		bookingProducer.sendQueueEnterResponse(queueEnterMessage);

		log.info("[Booking] 예약 생성 - 성공: | userId={}", command.userId());

		redisService.evictAllCaches("readBookings", command.userId().toString());
		redisService.evictAllCaches("adminReadBookings", command.userId().toString());
		return BookingCreateResponse.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "readBooking", key = "#command.bookingId()")
	public BookingReadResponse readBooking(BookingSingleTargetCommand command) {

		log.info("[Booking] 조회 - 시도: | userId={}", command.userId());

		Booking booking = findBookingByIdAndUserId(command.bookingId(), command.userId());
		TicketPerformanceClientResponse response = bookingClientService.getPerformanceInfo(
			booking.getPerformanceId(), booking.getPerformanceScheduleId(), booking.getScheduleSeatId());

		log.info("[Booking] 조회 - 성공: | userId={}", command.userId());

		return BookingReadResponse.toDto(booking, response);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "readBookings", key = "#command.userId() + ':' + #command.pageable().pageNumber + ':' + #command.pageable().pageSize + ':' + #command.pageable().sort")
	public BookingPageResponse readBookings(BookingPageReadCommand command) {

		log.info("[Booking] 조회 - 시도: | userId={}", command.userId());
		Page<Booking> page = bookingRepository.findAllByUserId(command.pageable(), command.userId());
		List<TicketPerformanceClientResponse> responses = page.getContent().stream()
			.map(e -> bookingClientService.getPerformanceInfo(e.getPerformanceId(), e.getPerformanceScheduleId(),
				e.getScheduleSeatId()))
			.toList();
		log.info("[Booking] 조회 - 성공: | userId={}", command.userId());

		return BookingPageResponse.toDto(page, responses);
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "readBooking", key = "#command.bookingId()"),
		@CacheEvict(value = "adminReadBooking", key = "#command.bookingId()")
	})
	public void cancelBooking(BookingSingleTargetCommand command) {

		log.info("[Booking] 예약 취소 - 시도: | userId={}", command.userId());

		Booking booking = findBookingByIdAndUserId(command.bookingId(), command.userId());
		BookingStatus status = booking.getBookingStatus();

		LocalDateTime startAt = bookingClientService.getPerformanceStartTime(booking.getPerformanceId(),
			booking.getPerformanceScheduleId()).startAt();
		LocalDateTime now = LocalDateTime.now();

		// 현재 시각이 공연 시작 하루 전 앞인지 확인
		if (now.isAfter(startAt.minusDays(1))) {
			log.warn(
				"[Booking] 예약 취소 - 실패: {} | userId={}",
				ResponseCode.BOOKING_CANCEL_NOT_ALLOWED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_CANCEL_NOT_ALLOWED_EXCEPTION);
		}

		if (status == BookingStatus.CANCELED) {
			log.warn(
				"[Booking] 예약 취소 - 실패: {} | userId={}",
				ResponseCode.BOOKING_ALREADY_CANCELED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_ALREADY_CANCELED_EXCEPTION);
		} else if (status == BookingStatus.COMPLETED) {
			// 환불 요청 전송
			PaymentRefundMessage message = PaymentRefundMessage.builder()
				.bookingId(booking.getId())
				.paymentId(booking.getPaymentId())
				.userId(booking.getUserId())
				.price(booking.getDiscountedPrice())
				.type(PaymentRefundMessage.MessageType.REQUEST)
				.build();

			bookingProducer.sendPaymentRefundRequest(message);

			log.info("[Booking] 예약 취소 - 환불 요청 전송: | userId={}", command.userId());
		}

		// 좌석 선점 해제 요청 보내기
		BookingSeatClientRequestDto dto = new BookingSeatClientRequestDto(
			booking.getPerformanceId(),
			booking.getPerformanceScheduleId(),
			booking.getScheduleSeatId()
		);
		BookingSeatClientResponseDto responseDto = bookingClientService.cancelSeatStatus(dto);

		if (responseDto.reserved()) {
			log.warn(
				"[Booking] 예약 취소 - 실패: {} | userId={}",
				ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION);
		}

		booking.cancel(command.userId());
		log.info("[Booking] 예약 취소 - 성공: | userId={}", command.userId());

		redisService.evictAllCaches("readBookings", command.userId().toString());
		redisService.evictAllCaches("adminReadBookings", command.userId().toString());
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "readBooking", key = "#command.bookingId()"),
		@CacheEvict(value = "adminReadBooking", key = "#command.bookingId()")
	})
	public void deleteBooking(BookingSingleTargetCommand command) {

		log.info("[Booking] 예약 삭제 - 시도: | userId={}", command.userId());

		Booking booking = findBookingByIdAndUserId(command.bookingId(), command.userId());
		BookingStatus status = booking.getBookingStatus();

		if (status == BookingStatus.PENDING) {
			log.warn(
				"[Booking] 예약 삭제 - 실패: {} | userId={}",
				ResponseCode.BOOKING_DELETE_NOT_ALLOWED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_DELETE_NOT_ALLOWED_EXCEPTION);
		}

		booking.delete(command.userId());
		log.info("[Booking] 예약 삭제 - 성공: | userId={}", command.userId());

		redisService.evictAllCaches("readBookings", command.userId().toString());
		redisService.evictAllCaches("adminReadBookings", command.userId().toString());
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "adminReadBooking", key = "#command.bookingId()")
	public AdminBookingReadResponse adminReadBooking(BookingSingleTargetCommand command) {

		log.info("[Booking] 관리자 예매 조회 - 시도: | userId={}", command.userId());

		String role = command.role();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			log.warn(
				"[Booking] 관리자 예매 조회 - 실패: {} | userId={}",
				ResponseCode.ACCESS_DENIED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.ACCESS_DENIED_EXCEPTION);
		}

		Booking booking = bookingAdminRepository.findById(command.bookingId())
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
		log.info("[Booking] 관리자 예매 조회 - 성공: | userId={}", command.userId());

		return AdminBookingReadResponse.toDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "adminReadBookings", key = "#command.queryUserId() + ':' + #command.pageable().pageNumber + ':' + #command.pageable().pageSize + ':' + #command.pageable().sort")
	public AdminBookingPageResponse adminReadBookings(BookingAdminPageReadCommand command) {

		log.info("[Booking] 관리자 예매 조회 - 시도: | userId={}", command.userId());

		String role = command.role();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			log.warn(
				"[Booking] 관리자 예매 조회 - 실패: {} | userId={}",
				ResponseCode.ACCESS_DENIED_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.ACCESS_DENIED_EXCEPTION);
		}

		if (command.queryUserId() == null) {
			log.warn(
				"[Booking] 관리자 예매 조회 - 실패: {} | userId={}",
				ResponseCode.BOOKING_QUERY_MISSING_EXCEPTION.getMessage(),
				command.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_QUERY_MISSING_EXCEPTION);
		}

		Page<Booking> page = bookingAdminRepository.findAll(command.pageable());
		log.info("[Booking] 관리자 예매 조회 - 성공: | userId={}", command.userId());

		return AdminBookingPageResponse.toDto(page);
	}

	@Override
	public void createPayment(BookingPaymentCommand command) {

		log.info("[Booking] 예매 결제 - 시도: | userId={}, bookingId={}", command.userId(), command.bookingId());

		Booking booking = findBookingByIdAndUserId(command.bookingId(), command.userId());

		boolean isUsedCoupon = command.couponId() != null;
		boolean isUsedMileage = command.mileage() != null && command.mileage() > 0;

		// 마일리지나 쿠폰을 사용한 경우 -> 비동기 차감 요청 이벤트 전송
		if (isUsedCoupon || isUsedMileage) {
			UserBenefitMessage benefitUsageRequestMessage = UserBenefitMessage.builder()
				.bookingId(command.bookingId())
				.userId(command.userId())
				.couponId(command.couponId())
				.mileage(command.mileage())
				.price(booking.getPrice())
				.build();

			bookingProducer.sendBenefitUsageRequest(benefitUsageRequestMessage);
			log.info(
				"[Booking] 예매 결제 - 쿠폰, 마일리지 사용 요청 전송: | userId={}, bookingId={}",
				command.userId(),
				command.bookingId()
			);
			return;
		}

		// 마일리지, 쿠폰을 사용하지 않은 경우 바로 결제 요청
		PaymentMessage message = PaymentMessage.builder()
			.bookingId(command.bookingId())
			.userId(command.userId())
			.price(booking.getPrice())
			.type(PaymentMessage.MessageType.REQUEST)
			.build();

		bookingProducer.sendPaymentRequest(message);
		log.info(
			"[Booking] 예매 결제 - 쿠폰, 마일리지 사용 없이 결제 요청 전송: | userId={}, bookingId={}",
			command.userId(),
			command.bookingId()
		);
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "readBooking", key = "#message.bookingId"),
		@CacheEvict(value = "adminReadBooking", key = "#message.bookingId")
	})
	public void updateBooking(PaymentMessage message) {

		log.info(
			"[Booking] 예매 결제 메시지 수신 - 시도: | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
		);

		Booking booking = bookingRepository.findById(message.getBookingId())
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
		Optional<BenefitUsageHistory> optional = benefitUsageHistoryRepository.findByBookingIdAndRefundedIsFalse(
			message.getBookingId());
		PaymentMessage.PaymentResultStatus status = message.getStatus();

		// 성공시
		if (status == PaymentMessage.PaymentResultStatus.SUCCESS) {
			Booking updated = booking.toBuilder()
				.bookingStatus(BookingStatus.COMPLETED)
				.paymentId(message.getPaymentId())
				.bookedAt(LocalDateTime.now())
				.build();
			updated.preUpdate(message.getUserId());

			bookingRepository.save(updated);

			// 티켓 생성 요청
			bookingProducer.sendTicketRequest(
				TicketRequestMessage.builder()
					.userId(booking.getUserId())
					.bookingId(booking.getId())
					.performanceId(booking.getPerformanceId())
					.performanceScheduleId(booking.getPerformanceScheduleId())
					.seatId(booking.getScheduleSeatId())
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
					.price(booking.getPrice())
					.status(UserBenefitMessage.UserBenefitStatus.SUCCESS)
					.build();

				bookingProducer.sendBenefitRefundRequest(benefitMessage);
				log.info(
					"[Booking] 예매 결제 메시지 수신 - 성공: 쿠폰, 마일리지 사용내역 전송 | userId={}, bookingId={}",
					message.getUserId(),
					message.getBookingId()
				);
			}

			log.info(
				"[Booking] 예매 결제 메시지 수신 - 성공: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);

			redisService.evictAllCaches("readBookings", message.getUserId().toString());
			redisService.evictAllCaches("adminReadBookings", message.getUserId().toString());
		} else {
			// 실패시 사용한 쿠폰, 마일리지 원복처리

			if (optional.isPresent()) {
				BenefitUsageHistory benefitUsageHistory = optional.get();
				benefitUsageHistory.refunded(message.getUserId());
			}
			log.warn(
				"[Booking] 예매 결제 메시지 수신 - 실패: {} | userId={}, bookingId={}",
				ResponseCode.BOOKING_PAYMENT_FAILED_EXCEPTION.getMessage(),
				message.getUserId(),
				message.getBookingId()
			);

			throw new BookingException(ResponseCode.BOOKING_PAYMENT_FAILED_EXCEPTION);
		}
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "readBooking", key = "#message.bookingId"),
		@CacheEvict(value = "adminReadBooking", key = "#message.bookingId")
	})
	public void createPayment(UserBenefitMessage message) {

		log.info(
			"[Booking] 예매 쿠폰, 마일리지 적용 메시지 수신 - 시도: | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
		);

		Booking booking = bookingRepository.findById(message.getBookingId())
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
		int price = booking.getPrice(); // 정가

		if (message.getStatus() == UserBenefitMessage.UserBenefitStatus.SUCCESS) {
			if (message.getDiscount() != null) {
				double discountAmount = price * (message.getDiscount() / 100.0);  // 할인 금액 계산
				price = (int)(price - discountAmount); // 할인된 가격 계산

				if (!isValidPrice(price)) {
					log.warn(
						"[Booking] 예매 쿠폰, 마일리지 적용 메시지 수신 - 실패: {} | userId={}, bookingId={}",
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
						"[Booking] 예매 쿠폰, 마일리지 적용 메시지 수신 - 실패: {} | userId={}, bookingId={}",
						ResponseCode.INVALID_MILEAGE.getMessage(),
						message.getUserId(),
						message.getBookingId()
					);
					throw new BookingException(ResponseCode.INVALID_MILEAGE);
				}
			}

			// 쿠폰, 마일리지 사용내역 저장
			BenefitUsageHistory history = BenefitUsageHistory.builder()
				.bookingId(booking.getId())
				.couponId(message.getCouponId())
				.mileage(message.getMileage())
				.usedAt(LocalDateTime.now())
				.refunded(false)
				.build();
			history.prePersist(message.getUserId());

			benefitUsageHistoryRepository.save(history);

			// 결제 요청
			PaymentMessage paymentMessage = PaymentMessage.builder()
				.bookingId(booking.getId())
				.userId(message.getUserId())
				.price(booking.getDiscountedPrice())
				.type(PaymentMessage.MessageType.REQUEST)
				.build();

			bookingProducer.sendPaymentRequest(paymentMessage);
			log.info(
				"[Booking] 예매 쿠폰, 마일리지 적용 메시지 수신 - 결제 요청 전송: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);

			booking.discount(price); // 할인가 업데이트

			log.info(
				"[Booking] 예매 쿠폰, 마일리지 적용 메시지 수신 - 성공: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);

			redisService.evictAllCaches("readBookings", message.getUserId().toString());
			redisService.evictAllCaches("adminReadBookings", message.getUserId().toString());
		} else {
			log.warn(
				"[Booking] 예매 쿠폰, 마일리지 적용 메시지 수신 - 실패: {} | userId={}, bookingId={}",
				ResponseCode.BOOKING_BENEFIT_USAGE_FAILED_EXCEPTION.getMessage(),
				message.getUserId(),
				message.getBookingId()
			);
			throw new BookingException(ResponseCode.BOOKING_BENEFIT_USAGE_FAILED_EXCEPTION);
		}
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "readBooking", key = "#message.bookingId"),
		@CacheEvict(value = "adminReadBooking", key = "#message.bookingId")
	})
	public void updateBooking(PaymentRefundMessage message) {

		log.info(
			"[Booking] 예매 환불 메시지 수신 - 시도: | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
		);

		if (message.getStatus() == PaymentRefundMessage.PaymentRefundStatus.SUCCESS) {
			Booking booking = bookingRepository.findById(message.getBookingId())
				.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
			booking.cancel(message.getUserId());

			Optional<BenefitUsageHistory> optional = benefitUsageHistoryRepository.findByBookingIdAndRefundedIsFalse(
				booking.getId());

			// 좌석 선점 취소 요청
			BookingSeatClientRequestDto dto = new BookingSeatClientRequestDto(
				booking.getPerformanceId(),
				booking.getPerformanceScheduleId(),
				booking.getScheduleSeatId()
			);
			BookingSeatClientResponseDto responseDto = bookingClientService.cancelSeatStatus(dto);

			if (responseDto.reserved()) {
				throw new BookingException(ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION);
			}

			// 쿠폰, 마일리지 원복 요청
			if (optional.isPresent()) {
				BenefitUsageHistory history = optional.get();

				UserBenefitMessage benefitMessage = UserBenefitMessage.builder()
					.bookingId(booking.getId())
					.userId(booking.getUserId())
					.couponId(history.getCouponId())
					.mileage(history.getMileage())
					.price(booking.getPrice())
					.status(UserBenefitMessage.UserBenefitStatus.REFUND)
					.build();
				bookingProducer.sendBenefitRefundRequest(benefitMessage);

				log.info(
					"[Booking] 예매 환불 메시지 수신 - 쿠폰, 마일리지 원복 요청 전송: | userId={}, bookingId={}",
					message.getUserId(),
					message.getBookingId()
				);
			}

			log.info(
				"[Booking] 예매 환불 메시지 수신 - 성공: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);

			redisService.evictAllCaches("readBookings", message.getUserId().toString());
			redisService.evictAllCaches("adminReadBookings", message.getUserId().toString());
		} else {
			log.warn(
				"[Booking] 예매 환불 메시지 수신 - 실패: {} | userId={}, bookingId={}",
				ResponseCode.BOOKING_REFUND_FAILED_EXCEPTION.getMessage(),
				message.getUserId(),
				message.getBookingId()
			);
			throw new BookingException(ResponseCode.BOOKING_REFUND_FAILED_EXCEPTION);
		}
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "readBooking", key = "#bookingId"),
		@CacheEvict(value = "adminReadBooking", key = "#bookingId")
	})
	public void expireBooking(UUID bookingId) {

		log.info("[Booking] 예매 만기 - 시도: | bookingId={}", bookingId);

		Booking booking = bookingRepository.findById(bookingId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
		BookingStatus status = booking.getBookingStatus();
		UUID system = UUID.fromString("00000000-0000-0000-0000-000000000000");

		if (status == BookingStatus.PENDING) {
			booking.cancel(system);
			booking.delete(system);

			BookingSeatClientRequestDto dto = new BookingSeatClientRequestDto(
				booking.getPerformanceId(),
				booking.getPerformanceScheduleId(),
				booking.getScheduleSeatId()
			);
			BookingSeatClientResponseDto responseDto = bookingClientService.cancelSeatStatus(dto);

			if (responseDto.reserved()) {
				log.warn(
					"[Booking] 예매 만기 - 실패: {} | bookingId={}",
					ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION.getMessage(),
					bookingId
				);
				throw new BookingException(ResponseCode.BOOKING_SEAT_CANCEL_FAILED_EXCEPTION);
			}

			log.info("[Booking] 예매 만기 - 성공: | bookingId={}", bookingId);

			redisService.evictAllCaches("readBookings", booking.getUserId().toString());
			redisService.evictAllCaches("adminReadBookings", booking.getUserId().toString());
		}
	}

	@Override
	@Transactional
	public void updateBenefitUsageHistory(UserBenefitMessage message) {

		log.info(
			"[Booking] 예매 쿠폰, 마일리지 사용 내역 환불처리 - 시도: | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
		);

		if (message.getStatus() == UserBenefitMessage.UserBenefitStatus.SUCCESS) {
			BenefitUsageHistory history = benefitUsageHistoryRepository.findByBookingIdAndRefundedIsFalse(
					message.getBookingId())
				.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_BENEFIT_USAGE_NOT_FOUND_EXCEPTION));

			history.refunded(message.getUserId());

			log.info(
				"[Booking] 예매 쿠폰, 마일리지 사용 내역 환불처리 - 성공: | userId={}, bookingId={}",
				message.getUserId(),
				message.getBookingId()
			);
		} else {
			log.warn(
				"[Booking] 예매 쿠폰, 마일리지 사용 내역 환불처리 - 실패: {} | userId={}, bookingId={}",
				ResponseCode.BOOKING_BENEFIT_USAGE_REFUND_FAILED_EXCEPTION.getMessage(),
				message.getUserId(),
				message.getBookingId()
			);
			throw new BookingException(ResponseCode.BOOKING_BENEFIT_USAGE_REFUND_FAILED_EXCEPTION);
		}
	}

	@Override
	@Transactional
	public void acceptFromQueue(BookingRequestMessage message) {

		log.info(
			"[Booking] 대기열에서 입장: | userId={}, performanceId={}, performanceScheduleId={}",
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
			message.getPerformanceScheduleId(), seatId
		);

		createBooking(command);
	}

	private boolean isValidPrice(int price) {
		return price > 0;
	}

	private Booking findBookingByIdAndUserId(UUID id, UUID userId) {
		return bookingRepository.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
	}
}