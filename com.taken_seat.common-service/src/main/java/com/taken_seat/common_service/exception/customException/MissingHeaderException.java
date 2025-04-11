package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class MissingHeaderException extends BaseException {
	public MissingHeaderException(ResponseCode errorCode) {
		super(errorCode);
	}

	public MissingHeaderException(ResponseCode errorCode, String message) {
		super(errorCode, message);
	}
}
