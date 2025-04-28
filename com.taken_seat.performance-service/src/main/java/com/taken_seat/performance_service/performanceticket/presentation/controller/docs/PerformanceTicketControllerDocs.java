package com.taken_seat.performance_service.performanceticket.presentation.controller.docs;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.performance_service.performanceticket.infrastructure.swagger.PerformanceTicketSwaggerDocs;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "공연 티켓 API", description = "스케줄별 공연 티켓 정보 조회 기능을 제공합니다.")
public interface PerformanceTicketControllerDocs {

	@PerformanceTicketSwaggerDocs.GetPerformanceTicket
	ResponseEntity<ApiResponseData<TicketPerformanceClientResponse>> getPerformanceInfo(
		UUID performanceId,
		UUID performanceScheduleId,
		UUID scheduleSeatId
	);
}
