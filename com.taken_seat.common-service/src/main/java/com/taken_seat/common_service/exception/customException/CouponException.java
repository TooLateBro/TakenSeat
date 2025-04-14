package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public class CouponException extends BaseException {
	public CouponException(ResponseCode errorCode) {
		super(errorCode);
	}

	public CouponException(ResponseCode errorCode, String message) {
		super(errorCode, message);
	}
}
