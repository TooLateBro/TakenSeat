package com.taken_seat.booking_service.booking.infrastructure.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.application.service.BookingCommandService;
import com.taken_seat.booking_service.booking.application.service.BookingProducer;
import com.taken_seat.booking_service.booking.application.service.RedissonService;
import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.BookingStatus;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.common.service.RedisService;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.QueueEnterMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingCommandServiceImpl implements BookingCommandService {

	private final BookingClientService bookingClientService;
	private final BookingProducer bookingProducer;
	private final BookingRepository bookingRepository;
	private final RedisService redisService;
	private final RedissonService redissonService;

	@Override
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

	private Booking findBookingByIdAndUserId(UUID id, UUID userId) {
		return bookingRepository.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
	}
}