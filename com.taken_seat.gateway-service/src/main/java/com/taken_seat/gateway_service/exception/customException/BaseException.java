package com.taken_seat.gateway_service.exception.customException;

import com.taken_seat.gateway_service.exception.enums.ResponseCode;

public class BaseException extends RuntimeException {

	private final ResponseCode errorCode;

	public BaseException(ResponseCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
	public ResponseCode getErrorCode() {
		return this.errorCode;
	}
}
