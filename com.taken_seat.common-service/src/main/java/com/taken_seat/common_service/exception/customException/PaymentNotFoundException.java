package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class PaymentNotFoundException extends BaseException {

	public PaymentNotFoundException(ResponseCode responseCode) {
		super(responseCode, responseCode.getMessage());

	}

	public PaymentNotFoundException(ResponseCode errorCode, String message) {
		super(errorCode, message);
	}
}
