package com.taken_seat.common_service.exception.handler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.exception.customException.BaseException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

@Component
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiResponseData<?>> handlerBaseExceptionException(BaseException e) {
		ResponseCode code = e.getErrorCode();

		// 직접 지정한 메시지가 있으면 사용, 없으면 enum의 기본 메시지 사용
		String message = Optional.ofNullable(e.getMessage())
			.filter(msg -> !msg.isBlank())
			.orElse(code.getMessage());

		return ResponseEntity.status(code.getStatus())
			.body(ApiResponseData.failure(code.getCode(), message));
	}

	// 잘못된 인자 전달 (ex: new IllegalArgumentException())
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponseData<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity.status(ResponseCode.ILLEGAL_ARGUMENT.getStatus())
			.body(ApiResponseData.failure(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ex.getMessage()));
	}

	// 객체 상태가 잘못된 경우 (ex: IllegalStateException)
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiResponseData<?>> handleIllegalStateException(IllegalStateException ex) {
		return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
			.body(ApiResponseData.failure(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage()));
	}

	// 널포인터 예외 처리
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ApiResponseData<?>> handleNullPointerException(NullPointerException ex) {
		return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
			.body(ApiResponseData.failure(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponseData<List<String>>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException ex) {

		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
			.map(error -> error.getField() + " : " + error.getDefaultMessage())
			.collect(Collectors.toList());

		return ResponseEntity.status(ResponseCode.VALIDATION_ERROR.getStatus())
			.body(ApiResponseData.of(ResponseCode.VALIDATION_ERROR.getCode(),
				"요청값이 유효하지 않습니다.", errors));
	}
}
