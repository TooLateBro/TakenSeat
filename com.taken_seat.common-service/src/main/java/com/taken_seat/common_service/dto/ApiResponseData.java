package com.taken_seat.common_service.dto;

import com.taken_seat.common_service.exception.enums.ResponseCode;

public record ApiResponseData<T>(Integer status, String message, T body) {
	// 성공 응답 (기본 메시지)
	public static <T> ApiResponseData<T> success(T data) {
		return new ApiResponseData<>(
			ResponseCode.SUCCESS.getCode(),
			ResponseCode.SUCCESS.getMessage(),
			data
		);
	}
	
	// 실패 응답
	public static <T> ApiResponseData<T> failure(Integer code, String message) {
		return new ApiResponseData<>(code, message, null);
	}

	// of 메서드
	public static <T> ApiResponseData<T> of(Integer code, String message, T data) {
		return new ApiResponseData<>(code, message, data);
	}
}