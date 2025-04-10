package com.taken_seat.common_service.exception.customException;

import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

	private final ResponseCode errorCode;

	// 에러 코드를 지정하는 생성자
	public BaseException(ResponseCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	// 에러 코드와 메시지를 받는 생성자
	public BaseException(ResponseCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

}
