package com.taken_seat.performance_service.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.taken_seat.performance_service.common.interceptor.CurrentUserInterceptor;

@Configuration
public class PerformanceWebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private CurrentUserInterceptor currentUserInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
			.addInterceptor(currentUserInterceptor)
			.addPathPatterns("/api/v1/**");
	}
}
