package com.taken_seat.common_service.dto;

public record ApiResponseDate<T>(int status, boolean success, T body) {
	// 성공 응답 생성
	public static <T> ApiResponseDate<T> success(int status, T body) {
		return new ApiResponseDate<>(status, true, body);
	}

	// 실패 응답 생성
	public static <T> ApiResponseDate<T> failure(int status, T body) {
		return new ApiResponseDate<>(status, false, body);
	}
}