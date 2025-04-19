package com.taken_seat.gateway_service.exception.enums;

import org.springframework.http.HttpStatus;

public enum ResponseCode {

	// Gateway
	ACCESS_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "엑세스 토큰이 없습니다."),
	EXPIRED_JWT(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "JWT 토큰이 만료되었습니다."),
	JWT_NOT_VALID(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "JWT 토큰이 유효하지 않습니다.");

	private final HttpStatus status;
	private final Integer code;
	private final String message;

	ResponseCode(HttpStatus status, Integer code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
	public HttpStatus getStatus() {
		return status;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}