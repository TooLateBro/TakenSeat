package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class PaymentException extends BaseException {

	public PaymentException(ResponseCode responseCode) {
		super(responseCode, responseCode.getMessage());

	}

	public PaymentException(ResponseCode errorCode, String message) {
		super(errorCode, message);
	}
}
