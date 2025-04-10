package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class PaymentHistoryNotFoundException extends BaseException {
	public PaymentHistoryNotFoundException(ResponseCode errorCode) {
		super(errorCode);
	}

	public PaymentHistoryNotFoundException(ResponseCode errorCode, String message) {
		super(errorCode, message);
	}
}
