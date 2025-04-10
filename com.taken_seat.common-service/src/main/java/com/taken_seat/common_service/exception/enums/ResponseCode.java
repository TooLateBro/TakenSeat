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
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에러"),
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "잘못된 입력값이 존재합니다."),
	ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), "권한이 없습니다."),

	// 자주 사용되는 기본 예외 처리
	ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
	ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "요청한 자원을 찾을 수 없습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.value(), "허용되지 않은 HTTP 메서드입니다."),

	// Payment
	PAYMENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 결제가 존재하지않습니다."),
	PAYMENT_HISTORY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "해당 결제의 내역이 존재하지않습니다.");

	private final HttpStatus status;
	private final Integer code;
	private final String message;
}
