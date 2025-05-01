package com.taken_seat.queue_service.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.queue_service.application.dto.QueueReqDto;
import com.taken_seat.queue_service.application.dto.TokenReqDto;
import com.taken_seat.queue_service.application.dto.TokenResDto;
import com.taken_seat.queue_service.application.service.QueueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {
	private final QueueService queueService;

	@PostMapping("")
	public ResponseEntity<ApiResponseData<TokenResDto>> enterQueue(@RequestBody @Valid QueueReqDto reqDto,
		AuthenticatedUser user) {
		return ResponseEntity.ok().body(ApiResponseData.success(queueService.enterQueue(reqDto, user.getUserId())));
	}

	@PostMapping("/getRank")
	public ResponseEntity<ApiResponseData<String>> getRank(@RequestBody @Valid TokenReqDto reqDto) {
		return ResponseEntity.ok().body(ApiResponseData.success(queueService.getRank(reqDto)));
	}
}