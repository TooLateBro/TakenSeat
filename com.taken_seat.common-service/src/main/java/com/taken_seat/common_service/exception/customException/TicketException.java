package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class TicketException extends BaseException {

	public TicketException(ResponseCode responseCode) {
		super(responseCode);
	}

	public TicketException(ResponseCode responseCode, String message) {
		super(responseCode, message);
	}
}