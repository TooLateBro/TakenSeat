package com.taken_seat.queue_service.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QueueScheduler {
    private static final int BATCH_SIZE = 100;
    private final QueueService queueService;

    @Scheduled(fixedRate = 10000)
    public void processQueueBatch() {
        queueService.processQueueBatch(BATCH_SIZE);
    }
}
