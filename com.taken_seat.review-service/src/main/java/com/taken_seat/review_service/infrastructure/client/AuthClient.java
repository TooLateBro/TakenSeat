package com.taken_seat.review_service.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.review_service.infrastructure.client.dto.UserNameDto;

@FeignClient(name = "${feign.client.auth.name}", url = "${feign.client.auth.name}")
public interface AuthClient {

	@GetMapping("/{userId}/username")
	ResponseEntity<ApiResponseData<UserNameDto>> getUserName(@PathVariable UUID userId);

}
