package com.taken_seat.review_service.infrastructure.swagger;

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
public @interface ReviewSwaggerDocs {

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "리뷰 등록", description = "공연 리뷰를 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "403", description = "접근 권한 없음"),
		@ApiResponse(responseCode = "404", description = "공연 정보 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@interface RegisterReview {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "리뷰 상세 조회", description = "리뷰 ID를 통해 리뷰 상세 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
		@ApiResponse(responseCode = "404", description = "리뷰 정보를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "id",
		description = "조회할 리뷰 ID(UUID 형식)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface GetPaymentDetail {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "리뷰 목록 검색", description = "검색 조건에 따라 리뷰 목록을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "q",
		description = "검색어",
		required = false,
		schema = @Schema(type = "string"),
		example = "너무 감동적입니다."
	)
	@Parameter(
		name = "category",
		description = "검색 카테고리 (예: 제목, 공연회차별 id, 작성자 이메일 등)",
		required = false,
		schema = @Schema(type = "string"),
		example = "user@gmail.com"
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
	@interface SearchReview {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "리뷰 수정", description = "리뷰 ID를 통해 리뷰 내용를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "403", description = "수정 권한 없음"),
		@ApiResponse(responseCode = "404", description = "리뷰 정보를 찾을 수 없음"),
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
	@interface UpdateReview {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "리뷰 삭제", description = "리뷰 ID를 통해 리뷰를 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
		@ApiResponse(responseCode = "403", description = "삭제 권한이 없음"),
		@ApiResponse(responseCode = "404", description = "리뷰 정보를 찾을 수 없음"),
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
	@interface DeleteReview {
	}

	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Operation(summary = "리뷰 좋아요 토글", description = "리뷰 ID를 통해 좋아요를 추가하거나 취소합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
		@ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음"),
		@ApiResponse(responseCode = "403", description = "접근 권한이 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@Parameter(
		name = "reviewId",
		description = "좋아요를 토글할 리뷰 ID(UUID 형식)",
		required = true,
		in = ParameterIn.PATH,
		schema = @Schema(type = "string", format = "uuid"),
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	@interface ToggleReviewLike {
	}
}
