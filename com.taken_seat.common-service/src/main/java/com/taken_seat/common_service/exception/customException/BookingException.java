package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class BookingException extends BaseException {

	public BookingException(ResponseCode responseCode) {
		super(responseCode);
	}

	public BookingException(ResponseCode responseCode, String message) {
		super(responseCode, message);
	}
}