package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class ReviewException extends BaseException {
	public ReviewException(ResponseCode errorCode) {
		super(errorCode);
	}

	public ReviewException(ResponseCode errorCode, String message) {
		super(errorCode, message);
	}
}
