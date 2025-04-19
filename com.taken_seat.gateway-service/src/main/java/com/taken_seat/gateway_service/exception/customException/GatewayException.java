package com.taken_seat.gateway_service.exception.customException;

import com.taken_seat.gateway_service.exception.enums.ResponseCode;

public class GatewayException extends BaseException {
	public GatewayException(ResponseCode errorCode) {
		super(errorCode);
	}
}
