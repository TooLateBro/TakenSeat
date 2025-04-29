package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.domain.BookingCommand;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;
import com.taken_seat.booking_service.booking.presentation.dto.response.SeatLayoutResponseDto;
import com.taken_seat.booking_service.common.client.PerformanceClient;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.BookingStatusDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingClientServiceImpl implements BookingClientService {

	private final PerformanceClient performanceClient;
	private final BookingRepository bookingRepository;

	@Override
	public BookingStatusDto getBookingStatus(UUID userId, UUID performanceId) {
		BookingCommand bookingCommand = bookingRepository.findByUserIdAndPerformanceId(userId, performanceId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));

		return new BookingStatusDto(bookingCommand.getBookingStatus().name());
	}

	@Override
	public BookingSeatClientResponseDto updateSeatStatus(BookingSeatClientRequestDto request) {
		ApiResponseData<BookingSeatClientResponseDto> response = performanceClient.updateSeatStatus(request);

		return response.body();
	}

	@Override
	public BookingSeatClientResponseDto cancelSeatStatus(BookingSeatClientRequestDto requestDto) {
		ApiResponseData<BookingSeatClientResponseDto> response = performanceClient.cancelSeatStatus(requestDto);

		return response.body();
	}

	@Override
	public PerformanceStartTimeDto getPerformanceStartTime(UUID performanceId, UUID performanceScheduleId) {
		ApiResponseData<PerformanceStartTimeDto> response = performanceClient.getPerformanceStartTime(performanceId,
			performanceScheduleId);

		return response.body();
	}

	@Override
	public SeatLayoutResponseDto getSeatLayout(UUID performanceScheduleId) {
		ApiResponseData<SeatLayoutResponseDto> response = performanceClient.getSeatLayout(performanceScheduleId);

		return response.body();
	}

	@Override
	public TicketPerformanceClientResponse getPerformanceInfo(UUID performanceId, UUID performanceScheduleId,
		UUID scheduleSeatId) {
		ApiResponseData<TicketPerformanceClientResponse> response = performanceClient.getPerformanceInfo(performanceId,
			performanceScheduleId, scheduleSeatId);

		return response.body();
	}
}