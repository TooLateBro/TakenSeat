package com.taken_seat.performance_service.common.interceptor;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.taken_seat.performance_service.common.context.CurrentUserContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CurrentUserInterceptor implements HandlerInterceptor {

	@Autowired
	private CurrentUserContext userContext;

	@Override
	public boolean preHandle(HttpServletRequest request,
		HttpServletResponse response,
		Object handler) {

		String userIdHeader = request.getHeader("X-User-Id");
		if (userIdHeader != null) {
			userContext.setUserId(UUID.fromString(userIdHeader));
		}
		return true;
	}
}
