package com.taken_seat.payment_service.infrastructure.swagger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Target({ElementType.METHOD}) // 메서드에만 적용할 수 있음
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지됨
@Documented // javadoc 과 같은 문서에 포함되도록 지정
public @interface PaymentSwaggerDocs {

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "결제 수동 등록", description = "주문을 수동으로 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "주문 접수 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 결제 금액이 요청됨"),
		@ApiResponse(responseCode = "403", description = "접근 권한이 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface RegisterPayment {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "결제 상세 조회", description = "결제 ID를 통해 결제 상세 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "결제 조회 성공"),
		@ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "조회할 결제 ID(UUID 형식)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface GetPaymentDetail {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "결제 목록 검색", description = "검색 조건에 따라 결제 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "결제 목록 조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "q",
		description = "검색어",
		required = false,
		schema = @Schema(type = "string"),
		example = "CREATED"
	)
	@Parameter(
		name = "category",
		description = "검색 카테고리 (예: 결제 상태, 결제승인일자, 환불일자 등)",
		required = false,
		schema = @Schema(type = "string"),
		example = "550e8400-e29b-41d4-a716-446655440000"
	)
	@Parameter(
		name = "page",
		description = "페이지 번호 (0부터 시작)",
		required = false,
		schema = @Schema(type = "integer"),
		example = "0"
	)
	@Parameter(
		name = "size",
		description = "페이지 크기",
		required = false,
		schema = @Schema(type = "integer"),
		example = "10"
	)
	@Parameter(
		name = "sort",
		description = "정렬 기준 필드",
		required = false,
		schema = @Schema(type = "string"),
		example = "createAt"
	)
	@Parameter(
		name = "order",
		description = "정렬 방식 (asc/desc)",
		required = false,
		schema = @Schema(type = "string"),
		example = "desc"
	)
	@interface SearchPayment {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "결제 정보 수정", description = "결제 ID를 통해 결제 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "결제 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 결제 금액이 요청됨"),
		@ApiResponse(responseCode = "403", description = "접근 권한이 없음"),
		@ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "수정할 결제 ID(UUID 형식)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface UpdatePayment {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "결제 삭제", description = "결제 ID를 통해 결제를 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "결제 삭제 성공"),
		@ApiResponse(responseCode = "403", description = "접근 권한이 없음"),
		@ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "삭제할 결제 ID(UUID 형식)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface DeletePayment {
	}
}
