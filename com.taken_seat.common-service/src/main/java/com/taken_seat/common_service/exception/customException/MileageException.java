package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class MileageException extends BaseException {
	public MileageException(ResponseCode errorCode) {
		super(errorCode);
	}

	public MileageException(ResponseCode errorCode, String message) {
		super(errorCode, message);
	}
}
