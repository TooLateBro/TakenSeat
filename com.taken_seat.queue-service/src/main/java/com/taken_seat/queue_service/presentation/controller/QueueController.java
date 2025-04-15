package com.taken_seat.queue_service.presentation.controller;


import com.taken_seat.queue_service.application.dto.QueueReqDto;
import com.taken_seat.queue_service.application.dto.RankDto;
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
    public ResponseEntity<?> enterQueue(@RequestBody @Valid QueueReqDto reqDto) {
        //추후 헤더에서 userID 받아야 함
        return ResponseEntity.ok().body(queueService.enterQueue(reqDto));
    }

    @PostMapping("/getRank")
    public ResponseEntity<?> getRank(@RequestBody @Valid RankDto reqDto) {
        return ResponseEntity.ok().body(queueService.getRank(reqDto.getToken()));
    }
}
