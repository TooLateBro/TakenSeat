package com.taken_seat.common_service.component;

import java.util.UUID;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.MissingHeaderException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

@Component
public class CustomUserArgumentResolver implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(AuthenticatedUser.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String userId = webRequest.getHeader("X-User-Id");
		String email = webRequest.getHeader("X-Email");
		String role = webRequest.getHeader("X-Role");

		// 필수 헤더 체크 및 예외 처리
		if (userId == null || email == null || role == null) {
			throw new MissingHeaderException(ResponseCode.MISSING_HEADER);
		}

		return new AuthenticatedUser(UUID.fromString(userId), email, role);
	}
}