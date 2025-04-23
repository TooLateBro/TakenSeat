package com.taken_seat.queue_service.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class QueueScheduler {
    private static final int BATCH_SIZE = 100;
    private final QueueService queueService;

    //대기열 관리(10초마다 실행)
    @Scheduled(fixedRate = 10000)
    public void processQueueBatch() {
        queueService.processQueueBatch(BATCH_SIZE);
    }

    //공연 Set 관리(1시간 마다 실행)
    @Scheduled(fixedRate = 3600000)
    public void performanceSetBatch() {
        queueService.performanceSetBatch();
    }
}
