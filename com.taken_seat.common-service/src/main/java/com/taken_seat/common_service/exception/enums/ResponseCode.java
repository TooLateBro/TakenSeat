package com.taken_seat.common_service.exception.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ResponseCode 는 API 응답 시 사용할 공통 예외처리 응답 코드 및 메시지를 작성하는 enum입니다.
 *
 * - HttpStatus : 응답 HTTP 상태 코드 ( 예: HttpStatus.BAD_REQUEST
 * - Integer code : 응답 코드
 * - String message : 사용자에게 전달할 에러 메시지 기본값
 *
 *사용 예시:
 * 1. 커스텀 예외를 던질 때:
 *    throw new CustomException(ResponseCode.ENTITY_NOT_FOUND);
 *
 * 2. 예외 메시지를 추가로 전달하고 싶을 때:
 *    throw new CustomException(ResponseCode.ENTITY_NOT_FOUND, "해당 상품을 찾을 수 없습니다.");
 *
 * 1번의 방법을 사용하면 기본 값 메시지가 전달되고 2번의 방법을 사용하면 직접 작성한 메시지가 반환됩니다.
 *
 * 자바에서 기본적으로 제공하는 예외 클래스는 핸들러에서 처리합니다.
 *
 */

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

	// 기본 응답
	SUCCESS(HttpStatus.OK, HttpStatus.OK.value(), "성공적으로 처리되었습니다."),
	SUCCESS_NO_CONTENT(HttpStatus.NO_CONTENT, HttpStatus.NO_CONTENT.value(), "성공적으로 처리되었습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에러"),
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "잘못된 입력값이 존재합니다."),
	ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "권한이 없습니다."),

	// 자주 사용되는 공통 예외 처리
	ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
	ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "요청한 자원을 찾을 수 없습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.value(), "허용되지 않은 HTTP 메서드입니다."),
	MISSING_HEADER(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "필수 요청 헤더가 누락되었습니다."),

	// Payment
	PAYMENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 결제가 존재하지않습니다."),
	PAYMENT_HISTORY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 결제의 내역이 존재하지않습니다."),

	// Review
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 리뷰가 존재하지않습니다."),
	REVIEW_ALREADY_WRITTEN(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "해당 공연에 대한 리뷰는 이미 작성하셨습니다."),
	FORBIDDEN_REVIEW_ACCESS(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "해당 리뷰에 접근할 권한이 없습니다."),
	BOOKING_NOT_COMPLETED(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "예매가 완료된 공연에 대해서만 리뷰를 작성할 수 있습니다."),
	EARLY_REVIEW(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "공연 시작 전에는 리뷰를 등록할 수 없습니다."),
	INVALID_LIKE_COUNT(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "좋아요 수는 0보다 작을 수 없습니다."),
	INVALID_COUPON(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "잘못된 쿠폰 사용입니다."),
	INVALID_MILEAGE(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "잘못된 마일리지 사용입니다."),
	CANNOT_REFUND(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "현재 상태에서는 환불이 불가능합니다."),

	// Performance
	PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 공연 정보를 찾을 수 없습니다."),

	// User
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 유저 정보를 찾을 수 없습니다."),
	USER_BAD_PASSWORD(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),
	USER_CONFLICT_EMAIL(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 존재하는 이메일 입니다."),

	// Mileage
	MILEAGE_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "마일리지가 존재하지 않습니다."),
	MILEAGE_EMPTY(HttpStatus.NO_CONTENT, HttpStatus.NO_CONTENT.value(), "마일리지가 부족합니다."),
	// Coupon
	COUPON_EXPIRED(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "쿠폰이 만료되었습니다."),
	COUPON_QUANTITY_EXCEPTION(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "쿠폰의 수량이 모두 소진 되었습니다."),
	COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "쿠폰이 존재하지 않습니다."),
	COUPON_EXISTS(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 존재하는 쿠폰 입니다."),
	COUPON_HAS_USER(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "이미 보유한 쿠폰 입니다."),

	// Booking
	BOOKING_ALREADY_CANCELED_EXCEPTION(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 취소된 예약입니다."),
	BOOKING_BENEFIT_USAGE_FAILED_EXCEPTION(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(),
		"쿠폰 또는 마일리지 사용을 실패했습니다."),
	BOOKING_BENEFIT_USAGE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(),
		"쿠폰, 마일리지 사용 내역이 없습니다."),
	BOOKING_BENEFIT_USAGE_REFUND_FAILED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,
		HttpStatus.INTERNAL_SERVER_ERROR.value(), "쿠폰, 마일리지 원복처리가 실패했습니다."),
	BOOKING_CANCEL_NOT_ALLOWED_EXCEPTION(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "해당 예매는 취소할 수 없습니다."),
	BOOKING_DELETE_NOT_ALLOWED_EXCEPTION(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "예약 대기중일땐 삭제할 수 없습니다."),
	BOOKING_DUPLICATED_EXCEPTION(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 동일한 예매가 존재합니다."),
	BOOKING_INTERRUPTED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
		"인터럽트가 발생했습니다."),
	BOOKING_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 예매는 존재하지 않습니다."),
	BOOKING_PAYMENT_FAILED_EXCEPTION(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "결제를 실패했습니다."),
	BOOKING_QUERY_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "쿼리 파라미터가 누락되었습니다."),
	BOOKING_REFUND_FAILED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
		"환불에 실패했습니다."),
	BOOKING_SEAT_CANCEL_FAILED_EXCEPTION(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "좌석 취소를 실패했습니다."),
	BOOKING_SEAT_LOCKED_EXCEPTION(HttpStatus.OK, HttpStatus.OK.value(), "이미 예약중인 좌석입니다."),
	BOOKING_SEAT_RESERVED_EXCEPTION(HttpStatus.OK, HttpStatus.OK.value(), "이미 선점된 좌석입니다."),
	BOOKING_SEAT_NONE_AVAILABLE_EXCEPTION(HttpStatus.OK, HttpStatus.OK.value(), "사용 가능한 좌석이 없습니다."),
	TICKET_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 티켓은 존재하지 않습니다."),
	TICKET_DUPLICATED_EXCEPTION(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 티켓이 존재합니다."),

	// Performance
	PERFORMANCE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 공연은 존재하지 않습니다."),
	PERFORMANCE_VALIDATION_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "동일 공연장에 겹치는 회차가 존재합니다."),
	PERFORMANCE_SCHEDULE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 공연 회차가 존재하지 않습니다"),
	PERFORMANCE_HALL_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 공연장은 존재하지 않습니다"),
	PERFORMANCE_HALL_ALREADY_EXISTS(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 존재하는 공연장입니다."),
	PERFORMANCE_HALL_DUPLICATE_SEAT(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "중복된 좌석이 존재합니다."),
	SEAT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 좌석은 존재하지 않습니다."),
	SEAT_STATUS_CHANGE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND.value(), "비활성 좌석은 상태를 변경할 수 없습니다."),
	SEAT_PRICE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "좌석 가격 정보를 찾을 수 없습니다."),
	SEAT_LOCK_FAILED(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "좌석 선점을 위한 락 획득에 실패했습니다."),
	SEAT_INTERRUPTED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(),
		"락 획득 중 인터럽트가 발생했습니다."),
	SEAT_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "좌석 상태를 찾을 수 없습니다."),

	//Queue
	QUEUE_UNAUTHORIZED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
	QUEUE_NOT_FOUND_TOKEN_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "대기열에 존재하지 않는 사용자입니다.");

	private final HttpStatus status;
	private final Integer code;
	private final String message;
}