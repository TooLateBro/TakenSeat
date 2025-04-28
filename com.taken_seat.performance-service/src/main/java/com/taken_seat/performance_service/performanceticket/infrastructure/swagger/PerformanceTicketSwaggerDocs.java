package com.taken_seat.performance_service.performanceticket.infrastructure.swagger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerformanceTicketSwaggerDocs {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(
		summary = "공연 티켓 정보 조회",
		description = "performanceId, performanceScheduleId, scheduleSeatId에 해당하는 티켓 정보를 조회합니다."
	)
	@Parameters({
		@Parameter(
			name = "performanceId",
			description = "조회할 공연 ID(UUID 형식)",
			required = true,
			in = ParameterIn.PATH,
			schema = @Schema(type = "string", format = "uuid"),
			example = "123e4567-e89b-12d3-a456-426614174000"
		),
		@Parameter(
			name = "performanceScheduleId",
			description = "조회할 공연 회차 ID(UUID 형식)",
			required = true,
			in = ParameterIn.PATH,
			schema = @Schema(type = "string", format = "uuid"),
			example = "223e4567-e89b-12d3-a456-426614174001"
		),
		@Parameter(
			name = "scheduleSeatId",
			description = "조회할 스케줄 좌석 ID(UUID 형식)",
			required = true,
			in = ParameterIn.PATH,
			schema = @Schema(type = "string", format = "uuid"),
			example = "323e4567-e89b-12d3-a456-426614174002"
		)
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "티켓 정보 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "티켓 정보를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface GetPerformanceTicket {
	}
}
