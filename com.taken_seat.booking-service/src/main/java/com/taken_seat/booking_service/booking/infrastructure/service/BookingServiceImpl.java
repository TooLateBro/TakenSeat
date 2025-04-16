package com.taken_seat.booking_service.booking.infrastructure.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.booking.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.application.dto.request.BookingPayRequest;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingReadResponse;
import com.taken_seat.booking_service.booking.application.service.BookingProducer;
import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.booking_service.booking.application.service.RedissonService;
import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.BookingStatus;
import com.taken_seat.booking_service.booking.domain.repository.BookingAdminRepository;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;
import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final RedissonService redissonService;
	private final BookingRepository bookingRepository;
	private final BookingAdminRepository bookingAdminRepository;
	private final BookingProducer bookingProducer;

	@Override
	@Transactional
	public BookingCreateResponse createBooking(AuthenticatedUser authenticatedUser, BookingCreateRequest request) {

		log.info("[Booking] 예매 요청 - 시작 - userId={}, performanceId={}, scheduleId={}, seatId={}",
			authenticatedUser.getUserId(), request.getPerformanceId(),
			request.getPerformanceScheduleId(), request.getSeatId());

		BookingSeatClientResponseDto responseDto = redissonService.tryHoldSeat(
			request.getPerformanceId(),
			request.getPerformanceScheduleId(),
			request.getSeatId()
		);

		Booking booking = Booking.builder()
			.userId(authenticatedUser.getUserId())
			.performanceId(request.getPerformanceId())
			.performanceScheduleId(request.getPerformanceScheduleId())
			.seatId(request.getSeatId())
			.price(responseDto.price())
			.discountedPrice(responseDto.price())
			.build();

		Booking saved = bookingRepository.save(booking);

		log.info(
			"[Booking] 예매 생성 - 성공 - bookingId={}, userId={}, performanceScheduleId={}, seatId={}, price={}",
			saved.getId(), saved.getUserId(), saved.getPerformanceScheduleId(), saved.getSeatId(), saved.getPrice());

		System.out.println("dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd" + saved.getId());

		return BookingCreateResponse.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public BookingReadResponse readBooking(AuthenticatedUser authenticatedUser, UUID id) {

		Booking booking = findBookingByIdAndUserId(id, authenticatedUser.getUserId());

		return BookingReadResponse.toDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public BookingPageResponse readBookings(AuthenticatedUser authenticatedUser, Pageable pageable) {

		Page<Booking> page = bookingRepository.findAllByUserId(pageable, authenticatedUser.getUserId());

		return BookingPageResponse.toDto(page);
	}

	@Override
	@Transactional
	public void updateBooking(AuthenticatedUser authenticatedUser, UUID id) {

		Booking booking = findBookingByIdAndUserId(id, authenticatedUser.getUserId());

		if (booking.getCanceledAt() != null) {
			throw new BookingException(ResponseCode.BOOKING_ALREADY_CANCELED_EXCEPTION);
		}

		booking.cancel();

		if (booking.getPaymentId() != null) {
			// TODO: 환불 요청 보내기
		}

		// TODO: 좌석 선점 해제 요청 보내기
	}

	@Override
	@Transactional
	public void deleteBooking(AuthenticatedUser authenticatedUser, UUID id) {

		Booking booking = findBookingByIdAndUserId(id, authenticatedUser.getUserId());

		if (booking.getCanceledAt() == null) {
			throw new BookingException(ResponseCode.BOOKING_NOT_CANCELED_EXCEPTION);
		}

		booking.delete(authenticatedUser.getUserId());
	}

	@Override
	@Transactional(readOnly = true)
	public AdminBookingReadResponse adminReadBooking(AuthenticatedUser authenticatedUser, UUID id) {

		String role = authenticatedUser.getRole();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			throw new BookingException(ResponseCode.ACCESS_DENIED_EXCEPTION);
		}

		Booking booking = bookingAdminRepository.findById(id)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));

		return AdminBookingReadResponse.toDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public AdminBookingPageResponse adminReadBookings(AuthenticatedUser authenticatedUser, Pageable pageable) {

		// TODO: Querydsl 을 적용하여 사용자ID 포함 동적 검색 적용하기
		String role = authenticatedUser.getRole();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			throw new BookingException(ResponseCode.ACCESS_DENIED_EXCEPTION);
		}

		Page<Booking> page = bookingAdminRepository.findAll(pageable);

		return AdminBookingPageResponse.toDto(page);
	}

	@Override
	public void createPayment(AuthenticatedUser authenticatedUser, UUID id, BookingPayRequest request) {

		Booking booking = findBookingByIdAndUserId(id, authenticatedUser.getUserId());

		boolean isUsedCoupon = request.getCouponId() != null;
		boolean isUsedMileage = request.getMileage() != null && request.getMileage() > 0;

		// 마일리지나 쿠폰을 사용한 경우 -> 비동기 차감 요청 이벤트 전송
		if (isUsedCoupon || isUsedMileage) {
			UserBenefitMessage benefitUsageRequestMessage = UserBenefitMessage.builder()
				.bookingId(id)
				.userId(authenticatedUser.getUserId())
				.couponId(request.getCouponId())
				.mileage(request.getMileage())
				.build();

			bookingProducer.sendBenefitUsageRequest(benefitUsageRequestMessage);
			return;
		}

		// 마일리지, 쿠폰을 사용하지 않은 경우 바로 결제 요청
		PaymentMessage message = PaymentMessage.builder()
			.bookingId(id)
			.userId(authenticatedUser.getUserId())
			.price(booking.getPrice())
			.type(PaymentMessage.MessageType.REQUEST)
			.build();

		bookingProducer.sendPaymentRequestEvent(message);
	}

	@Override
	@Transactional
	public void updateBooking(PaymentMessage message) {

		PaymentMessage.PaymentResultStatus status = message.getStatus();
		// 성공시
		if (status == PaymentMessage.PaymentResultStatus.SUCCESS) {
			Booking booking = bookingRepository.findById(message.getBookingId())
				.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));

			Booking updated = booking.toBuilder()
				.bookingStatus(BookingStatus.COMPLETED)
				.paymentId(message.getPaymentId())
				.bookedAt(LocalDateTime.now())
				.build();

			bookingRepository.save(updated);
			bookingProducer.sendPaymentCompleteEvent(
				TicketRequestMessage.builder()
					.userId(booking.getUserId())
					.bookingId(booking.getId())
					.build()
			);
		} else {
			throw new BookingException(ResponseCode.BOOKING_PAYMENT_FAILED_EXCEPTION);
		}
	}

	@Override
	@Transactional
	public void createPayment(UserBenefitMessage message) {

		Booking booking = bookingRepository.findById(message.getBookingId())
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
		int price = booking.getPrice(); // 정가

		if (message.getDiscount() != null) {
			double discountAmount = price * (message.getDiscount() / 100.0);  // 할인 금액 계산
			price = (int)(price - discountAmount); // 할인된 가격 계산

			if (!isValidPrice(price)) {
				throw new BookingException(ResponseCode.INVALID_COUPON);
			}
		}

		// 마일리지 차감
		if (message.getMileage() != null) {
			price -= message.getMileage();

			if (!isValidPrice(price)) {
				throw new BookingException(ResponseCode.INVALID_MILEAGE);
			}
		}
		booking.discount(price); // 할인가 업데이트

		// 결제 요청
		PaymentMessage paymentMessage = PaymentMessage.builder()
			.bookingId(booking.getId())
			.userId(message.getUserId())
			.price(booking.getDiscountedPrice())
			.type(PaymentMessage.MessageType.REQUEST)
			.build();

		bookingProducer.sendPaymentRequestEvent(paymentMessage);
	}

	private boolean isValidPrice(int price) {
		return price > 0;
	}

	private Booking findBookingByIdAndUserId(UUID id, UUID userId) {
		return bookingRepository.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
	}
}