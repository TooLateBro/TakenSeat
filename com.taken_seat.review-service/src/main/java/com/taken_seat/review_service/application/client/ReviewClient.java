package com.taken_seat.review_service.application.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.taken_seat.review_service.infrastructure.client.dto.BookingStatusDto;
import com.taken_seat.review_service.infrastructure.client.dto.PerformanceEndTimeDto;

@Component
public interface ReviewClient {

	// 예매의 상태를 조회
	BookingStatusDto getBookingStatus(UUID userId, UUID performanceId);

	// 공연 종료 시간 조회
	PerformanceEndTimeDto getPerformanceEndTime(UUID performanceId, UUID performanceScheduleId);

}
