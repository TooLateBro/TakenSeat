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
public @interface PerformanceSwaggerDocs {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연 등록", description = "새로운 공연 정보를 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "공연 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface CreatePerformance {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(
		summary = "공연 목록 조회",
		description = "검색 필터(제목, 날짜 범위, 상태)와 페이징·정렬 정보를 기반으로 공연 목록을 조회합니다."
	)
	@Parameters({
		@Parameter(
			name = "title",
			description = "공연 제목(부분 일치)",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "string"),
			example = "뮤지컬"
		),
		@Parameter(
			name = "startAt",
			description = "검색 시작일시(ISO 8601, 예: 2025-05-01T00:00:00)",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "string", format = "date-time"),
			example = "2025-05-01T00:00:00"
		),
		@Parameter(
			name = "endAt",
			description = "검색 종료일시(ISO 8601, 예: 2025-05-31T23:59:59)",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "string", format = "date-time"),
			example = "2025-05-31T23:59:59"
		),
		@Parameter(
			name = "status",
			description = "공연 상태 (예: CREATED, PUBLISHED, CANCELLED 등)",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "string"),
			example = "CREATED"
		),

		@Parameter(
			name = "page",
			description = "페이지 번호(0부터 시작)",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "integer", format = "int32"),
			example = "0"
		),
		@Parameter(
			name = "size",
			description = "페이지 크기",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "integer", format = "int32"),
			example = "10"
		),
		@Parameter(
			name = "sort",
			description = "정렬 기준(필드,방식)",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "string"),
			example = "startAt,desc"
		)
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface GetPerformanceList {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연 상세 조회", description = "공연 ID를 기반으로 상세 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상세 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "조회할 공연 ID(UUID 형식)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface GetPerformanceDetail {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연 정보 수정", description = "공연 ID를 기반으로 공연 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "수정할 공연 ID(UUID 형식)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface UpdatePerformance {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연 삭제", description = "공연 ID를 기반으로 공연을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공 (No Content)"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "삭제할 공연 ID(UUID 형식)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface DeletePerformance {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연 상태 변경", description = "공연 ID를 기반으로 공연 상태를 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상태 변경 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "상태를 변경할 공연 ID(UUID 형식)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface UpdatePerformanceStatus {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연 회차 삭제", description = "공연 ID 및 회차 ID를 기반으로 특정 회차를 삭제합니다.")
	@Parameters({
		@Parameter(
			name = "performanceId",
			description = "공연 ID(UUID 형식)",
			required = true,
			in = ParameterIn.PATH,
			schema = @Schema(type = "string", format = "uuid"),
			example = "123e4567-e89b-12d3-a456-426614174000"
		),
		@Parameter(
			name = "performanceScheduleId",
			description = "공연 회차 ID(UUID 형식)",
			required = true,
			in = ParameterIn.PATH,
			schema = @Schema(type = "string", format = "uuid"),
			example = "223e4567-e89b-12d3-a456-426614174001"
		)
	})
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공 (No Content)"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "공연 또는 회차를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface DeletePerformanceSchedule {
	}
}
