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
import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.application.service.BookingCommandService;
import com.taken_seat.booking_service.booking.application.service.BookingConsumerService;
import com.taken_seat.booking_service.booking.application.service.BookingProducer;
import com.taken_seat.booking_service.booking.domain.BenefitUsageHistory;
import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.BookingStatus;
import com.taken_seat.booking_service.booking.domain.repository.BenefitUsageHistoryRepository;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;
import com.taken_seat.booking_service.booking.presentation.dto.response.SeatLayoutResponseDto;
import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.booking_service.common.service.RedisService;
import com.taken_seat.common_service.aop.TrackLatency;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingConsumerServiceImpl implements BookingConsumerService {

	private final BenefitUsageHistoryRepository benefitUsageHistoryRepository;
	private final BookingClientService bookingClientService;
	private final BookingProducer bookingProducer;
	private final BookingRepository bookingRepository;
	private final RedisService redisService;
	private final BookingCommandService bookingCommandService;

	@TrackLatency(
		value = "booking_consumer_payment_create_seconds",
		description = "예매 쿠폰·마일리지 적용 및 결제 요청 처리 시간(초)"
	)
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

		bookingCommandService.createBooking(command);
	}

	private boolean isValidPrice(int price) {
		return price > 0;
	}
}