package com.taken_seat.performance_service.performance.infrastructure.swagger;

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
public @interface PerformanceClientSwaggerDocs {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "좌석 상태 변경", description = "좌석 예약 시 좌석 상태를 AVAILABLE -> SOLDOUT 로 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "변경 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface UpdateSeatStatus {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "좌석 상태 취소", description = "예약을 취소하여 좌석 상태를 SOLDOUT -> AVAILABLE 로 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "취소 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface CancelSeatStatus {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "좌석 배치도 조회", description = "공연 스케줄 ID로 좌석 배치도를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "performanceScheduleId",
		description = "공연 회차 ID(UUID)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "223e4567-e89b-12d3-a456-426614174001"
	)
	@interface GetSeatLayout {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연 종료 시간 조회", description = "리뷰 작성 가능 여부 판단에 사용합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameters({
		@Parameter(
			name = "performanceId",
			description = "공연 ID(UUID)",
			required = true,
			in = ParameterIn.PATH,
			schema = @Schema(type = "string", format = "uuid"),
			example = "123e4567-e89b-12d3-a456-426614174000"
		),
		@Parameter(
			name = "performanceScheduleId",
			description = "공연 회차 ID(UUID)",
			required = true,
			in = ParameterIn.PATH,
			schema = @Schema(type = "string", format = "uuid"),
			example = "223e4567-e89b-12d3-a456-426614174001"
		)
	})
	@interface GetPerformanceEndTime {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연 시작 시간 조회", description = "환불 가능 여부 판단에 사용합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameters({
		@Parameter(
			name = "performanceId",
			description = "공연 ID(UUID)",
			required = true,
			in = ParameterIn.PATH,
			schema = @Schema(type = "string", format = "uuid"),
			example = "123e4567-e89b-12d3-a456-426614174000"
		),
		@Parameter(
			name = "performanceScheduleId",
			description = "공연 회차 ID(UUID)",
			required = true,
			in = ParameterIn.PATH,
			schema = @Schema(type = "string", format = "uuid"),
			example = "223e4567-e89b-12d3-a456-426614174001"
		)
	})
	@interface GetPerformanceStartTime {
	}
}
