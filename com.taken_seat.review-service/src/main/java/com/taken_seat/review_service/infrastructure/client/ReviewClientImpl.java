package com.taken_seat.review_service.infrastructure.client;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.client.ReviewClient;
import com.taken_seat.review_service.infrastructure.client.dto.BookingStatusDto;
import com.taken_seat.review_service.infrastructure.client.dto.PerformanceEndTimeDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewClientImpl implements ReviewClient {

	private final BookingClient bookingClient;
	private final PerformanceClient performanceClient;

	@Override
	public BookingStatusDto getBookingStatus(UUID userId, UUID performanceId) {

		if (userId == null || performanceId == null) {
			return null;
		}

		ResponseEntity<ApiResponseData<BookingStatusDto>> bookingStatus = bookingClient.getBookingStatus(userId,
			performanceId);

		if (bookingStatus.getBody() == null || bookingStatus.getBody().body() == null) {
			throw new ReviewException(ResponseCode.BOOKING_NOT_COMPLETED);
		}

		return bookingStatus.getBody().body();
	}

	@Override
	public PerformanceEndTimeDto getPerformanceEndTime(UUID performanceId, UUID performanceScheduleId) {

		if (performanceId == null) {
			throw new ReviewException(ResponseCode.PERFORMANCE_NOT_FOUND);
		}

		ResponseEntity<ApiResponseData<PerformanceEndTimeDto>> performanceEndTime = performanceClient.getPerformanceEndTime(
			performanceId, performanceScheduleId);

		if (performanceEndTime.getBody() == null || performanceEndTime.getBody().body() == null) {
			throw new ReviewException(ResponseCode.PERFORMANCE_NOT_FOUND);
		}

		return performanceEndTime.getBody().body();
	}
}
