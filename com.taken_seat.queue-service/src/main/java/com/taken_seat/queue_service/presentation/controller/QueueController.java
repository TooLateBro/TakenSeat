package com.taken_seat.queue_service.presentation.controller;


import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.queue_service.application.dto.QueueReqDto;
import com.taken_seat.queue_service.application.dto.TokenReqDto;
import com.taken_seat.queue_service.application.service.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {
    private final QueueService queueService;

    @PostMapping("")
    public ResponseEntity<?> enterQueue(@RequestBody @Valid QueueReqDto reqDto, AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok().body(queueService.enterQueue(reqDto, authenticatedUser.getUserId()));
    }

    @PostMapping("/getRank")
    public ResponseEntity<?> getRank(@RequestBody @Valid TokenReqDto reqDto) {
        return ResponseEntity.ok().body(queueService.getRank(reqDto));
    }
}
