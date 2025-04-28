package com.taken_seat.performance_service.common.context;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import lombok.Data;

@Component
@Scope(
	value = WebApplicationContext.SCOPE_REQUEST,
	proxyMode = ScopedProxyMode.TARGET_CLASS
)
@Data
public class CurrentUserContext {

	private UUID userId;
}
