package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class AuthException extends BaseException {
	public AuthException(ResponseCode errorCode) {
		super(errorCode);
	}

	public AuthException(ResponseCode errorCode, String message) {
		super(errorCode, message);
	}
}
