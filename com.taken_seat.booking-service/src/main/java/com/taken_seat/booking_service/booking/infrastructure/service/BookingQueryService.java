package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.booking.application.dto.query.BookingAdminListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingReadQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingStatusQuery;
import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.application.service.BookingProducer;
import com.taken_seat.booking_service.booking.domain.BookingQuery;
import com.taken_seat.booking_service.booking.domain.BookingStatus;
import com.taken_seat.booking_service.booking.domain.repository.BookingQueryRepository;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;
import com.taken_seat.booking_service.common.message.BookingCommandMessage;
import com.taken_seat.booking_service.common.message.BookingPaymentRequestMessage;
import com.taken_seat.booking_service.common.message.BookingQueryMessage;
import com.taken_seat.booking_service.common.service.RedisService;
import com.taken_seat.common_service.dto.response.BookingStatusDto;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.PaymentMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingQueryService {

	private final BookingClientService bookingClientService;
	private final BookingQueryRepository bookingQueryRepository;
	private final BookingProducer bookingProducer;
	private final RedisService redisService;

	@Transactional(readOnly = true)
	@Cacheable(value = "readBooking", key = "#query.bookingId()")
	public BookingReadResponse readBooking(BookingReadQuery query) {

		log.info("[BookingQuery] 조회 - 시도: | userId={}, bookingId={}", query.userId(), query.bookingId());

		BookingQuery bookingQuery = findBookingByIdAndUserId(query.bookingId(), query.userId());

		log.info("[BookingQuery] 조회 - 성공: | userId={}, bookingId={}", query.userId(), query.bookingId());

		return BookingReadResponse.toDto(bookingQuery);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "readBookings", key = "#query.userId() + ':' + #query.pageable().pageNumber + ':' + #query.pageable().pageSize + ':' + #query.pageable().sort")
	public BookingPageResponse readBookings(BookingListQuery query) {

		log.info("[BookingQuery] 조회 - 시도: | userId={}", query.userId());

		Page<BookingQuery> page = bookingQueryRepository.findAllByUserId(query.pageable(), query.userId());

		log.info("[BookingQuery] 조회 - 성공: | userId={}", query.userId());

		return BookingPageResponse.toDto(page);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "adminReadBooking", key = "#query.bookingId()")
	public AdminBookingReadResponse adminReadBooking(BookingReadQuery query) {

		log.info("[BookingQuery] 관리자 예매 조회 - 시도: | userId={}, bookingId={}", query.userId(), query.bookingId());

		BookingQuery bookingQuery = findBookingById(query.bookingId());

		log.info("[BookingQuery] 관리자 예매 조회 - 성공: | userId={}, bookingId={}", query.userId(), query.bookingId());

		return AdminBookingReadResponse.toDto(bookingQuery);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "adminReadBookings", key = "#query.queryUserId() + ':' + #query.pageable().pageNumber + ':' + #query.pageable().pageSize + ':' + #query.pageable().sort")
	public AdminBookingPageResponse adminReadBookings(BookingAdminListQuery query) {

		log.info("[BookingCommand] 관리자 예매 조회 - 시도: | userId={}", query.userId());

		Page<BookingQuery> page = bookingQueryRepository.findAllByAdmin(query.pageable(), query.queryUserId());

		log.info("[BookingCommand] 관리자 예매 조회 - 성공: | userId={}", query.userId());

		return AdminBookingPageResponse.toDto(page);
	}

	@Transactional(readOnly = true)
	public BookingStatusDto getBookingStatus(BookingStatusQuery query) {
		BookingQuery bookingQuery = bookingQueryRepository.findByUserIdAndPerformanceId(query.userId(),
			query.performanceId()).orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));

		return new BookingStatusDto(bookingQuery.getBookingStatus().name());
	}

	@Transactional
	public void receiveBookingCreatedEvent(BookingCommandMessage event) {

		log.info("[BookingQuery] 예매 생성 이벤트 수신 - 시도: | userId={}, bookingId={}", event.userId(), event.id());

		TicketPerformanceClientResponse response = bookingClientService.getPerformanceInfo(event.performanceId(),
			event.performanceScheduleId(), event.scheduleSeatId());

		BookingQuery bookingQuery = toQuery(event, response);
		bookingQuery.create(event);

		bookingQueryRepository.save(bookingQuery);

		redisService.evictAllCaches("readBookings", event.userId().toString());
		redisService.evictAllCaches("adminReadBookings", event.userId().toString());

		log.info("[BookingQuery] 예매 생성 이벤트 수신 - 성공: | userId={}, bookingId={}", event.userId(), event.id());
	}

	@Transactional
	@Caching(evict = {
		@CacheEvict(value = "readBooking", key = "#event.id()"),
		@CacheEvict(value = "adminReadBooking", key = "#event.id()")
	})
	public void receiveBookingUpdatedEvent(BookingCommandMessage event) {

		log.info("[BookingQuery] 예매 수정 이벤트 수신 - 시도: | userId={}, bookingId={}", event.userId(), event.id());

		BookingQuery bookingQuery = bookingQueryRepository.findById(event.id())
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));

		bookingQuery.update(event);

		redisService.evictAllCaches("readBookings", event.userId().toString());
		redisService.evictAllCaches("adminReadBookings", event.userId().toString());

		log.info("[BookingQuery] 예매 수정 이벤트 수신 - 성공: | userId={}, bookingId={}", event.userId(), event.id());
	}

	@Transactional(readOnly = true)
	public void receiveBookingCompletedMessage(UUID bookingId) {

		log.info("[BookingQuery] 티켓 생성 메시지 수신 - 시도: | bookingId={}", bookingId);

		BookingQuery bookingQuery = findBookingById(bookingId);

		bookingProducer.sendTicketRequestMessage(toMessage(bookingQuery));

		log.info("[BookingQuery] 티켓 생성 메시지 수신 - 성공: | bookingId={}", bookingId);
	}

	@Transactional(readOnly = true)
	public void receiveBookingPaymentRequestMessage(BookingPaymentRequestMessage message) {

		log.info("[BookingQuery] 결제 요청 메시지 수신 - 시도: | bookingId={}", message.bookingId());

		BookingQuery bookingQuery = findBookingById(message.bookingId());

		PaymentMessage paymentMessage = PaymentMessage.builder()
			.bookingId(bookingQuery.getId())
			.userId(bookingQuery.getUserId())
			.amount(message.amount())
			.orderName(bookingQuery.getTitle())
			.type(PaymentMessage.MessageType.REQUEST)
			.build();
		bookingProducer.sendPaymentMessage(paymentMessage);

		log.info("[BookingQuery] 결제 요청 메시지 수신 - 성공: | bookingId={}", message.bookingId());
	}

	private BookingQuery findBookingById(UUID id) {
		return bookingQueryRepository.findById(id)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
	}

	private BookingQuery findBookingByIdAndUserId(UUID id, UUID userId) {
		return bookingQueryRepository.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
	}

	private BookingQuery toQuery(BookingCommandMessage event, TicketPerformanceClientResponse response) {
		return BookingQuery.builder()
			.id(event.id())
			.userId(event.userId())
			.performanceId(event.performanceId())
			.performanceScheduleId(event.performanceScheduleId())
			.scheduleSeatId(event.scheduleSeatId())
			.paymentId(event.paymentId())
			.price(event.price())
			.discountedPrice(event.discountedPrice())
			.bookingStatus(BookingStatus.valueOf(event.bookingStatus()))
			.bookedAt(event.bookedAt())
			.canceledAt(event.canceledAt())
			.title(response.title())
			.name(response.name())
			.address(response.address())
			.rowNumber(response.rowNumber())
			.seatNumber(response.seatNumber())
			.seatType(response.seatType())
			.startAt(response.startAt())
			.endAt(response.endAt())
			.build();
	}

	private BookingQueryMessage toMessage(BookingQuery query) {
		return new BookingQueryMessage(
			query.getId(),
			query.getUserId(),
			query.getPerformanceId(),
			query.getPerformanceScheduleId(),
			query.getScheduleSeatId(),
			query.getPaymentId(),
			query.getPrice(),
			query.getDiscountedPrice(),
			query.getBookingStatus().name(),
			query.getBookedAt(),
			query.getCanceledAt(),
			query.getTitle(),
			query.getName(),
			query.getAddress(),
			query.getRowNumber(),
			query.getSeatNumber(),
			query.getSeatType(),
			query.getStartAt(),
			query.getEndAt(),
			query.getCreatedAt(),
			query.getCreatedBy(),
			query.getUpdatedAt(),
			query.getUpdatedBy(),
			query.getDeletedAt(),
			query.getDeletedBy()
		);
	}
}