package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class PerformanceException extends BaseException {

	public PerformanceException(ResponseCode responseCode) {
		super(responseCode);
	}

	public PerformanceException(ResponseCode responseCode, String message) {
		super(responseCode, message);
	}
}
