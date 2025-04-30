package com.taken_seat.queue_service.presentation.controller;


import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.queue_service.application.dto.QueueReqDto;
import com.taken_seat.queue_service.application.dto.TokenReqDto;
import com.taken_seat.queue_service.application.dto.TokenResDto;
import com.taken_seat.queue_service.application.service.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {
    private final QueueService queueService;

    @PostMapping("")
    public ResponseEntity<ApiResponseData<TokenResDto>> enterQueue(@RequestBody @Valid QueueReqDto reqDto) {
        return ResponseEntity.ok().body(ApiResponseData.success(queueService.enterQueue(reqDto, UUID.randomUUID())));
    }

    @PostMapping("/getRank")
    public ResponseEntity<ApiResponseData<String>> getRank(@RequestBody @Valid TokenReqDto reqDto) {
        return ResponseEntity.ok().body(ApiResponseData.success(queueService.getRank(reqDto)));
    }
}
