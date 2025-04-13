package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class PaymentHistoryException extends BaseException {
	public PaymentHistoryException(ResponseCode errorCode) {
		super(errorCode);
	}

	public PaymentHistoryException(ResponseCode errorCode, String message) {
		super(errorCode, message);
	}
}
