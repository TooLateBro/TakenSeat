package com.taken_seat.performance_service.performancehall.infrastructure.swagger;

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
public @interface PerformanceHallSwaggerDocs {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연장 등록", description = "새로운 공연장을 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "공연장 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface CreateHall {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연장 목록 조회", description = "필터 및 페이징 조건에 따라 공연장 목록을 조회합니다.")
	@Parameters({
		@Parameter(
			name = "name",
			description = "공연장 이름(부분 일치)",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "string"),
			example = "서울 올림픽홀"
		),
		@Parameter(
			name = "address",
			description = "공연장 주소(부분 일치)",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "string"),
			example = "송파구 올림픽로 424"
		),
		@Parameter(
			name = "minSeats",
			description = "최소 좌석 수",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "integer", format = "int32"),
			example = "100"
		),
		@Parameter(
			name = "maxSeats",
			description = "최대 좌석 수",
			in = ParameterIn.QUERY,
			schema = @Schema(type = "integer", format = "int32"),
			example = "500"
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
			example = "name,asc"
		)
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "목록 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface GetHallList {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연장 상세 조회", description = "공연장 ID로 상세 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상세 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "공연장을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "조회할 공연장 ID(UUID)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface GetHallDetail {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연장 정보 수정", description = "공연장 ID로 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "공연장을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "수정할 공연장 ID(UUID)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface UpdateHall {
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "공연장 삭제", description = "공연장 ID로 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공 (No Content)"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증되지 않음"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "공연장을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "삭제할 공연장 ID(UUID)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface DeleteHall {
	}
}