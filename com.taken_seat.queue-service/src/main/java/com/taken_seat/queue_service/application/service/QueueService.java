package com.taken_seat.queue_service.application.service;

import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.queue_service.application.dto.QueueReqDto;
import com.taken_seat.queue_service.infrastructure.jwt.JwtImpl;
import com.taken_seat.queue_service.infrastructure.messaging.QueueKafkaProducerImpl;
import com.taken_seat.queue_service.infrastructure.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final JwtImpl jwt;
    private final QueueRepository queueRepository;
    private final QueueKafkaProducerImpl kafkaProducer;

    public String enterQueue(QueueReqDto reqDto) {
        String token = jwt.createAccessToken(UUID.randomUUID(), reqDto.getPerformanceId(), reqDto.getPerformanceScheduleId());
        //각 공연마다 대기열을 만들어 관리하기 위해 공연 UUID를 Key로 설정
        String key = reqDto.getPerformanceId().toString();
        //timestamp를 통해 대기 순서 보장
        long timestamp = System.currentTimeMillis();

        //기존에 존재하는 토큰이라면(재입장 시) -> 기존 토큰 삭제 후 대기열 맨 뒤로 보내기
        if (queueRepository.setIsMember(key, token)) {
            queueRepository.exitQueue(token, key);
        }

        // 토큰 재/신규 추가
        queueRepository.enterQueue(token, key, timestamp);
        queueRepository.addUser(key, token);

        //공연이 공연 관리 set에 존재하지 않는다면 넣어주기
        if(!queueRepository.setIsPerformance(key)) {
            queueRepository.addActivePerformance(key);
        }

        //이 토큰을 프론트에서 지니고 있다가 유저 랭크 조회 시 해당 토큰을 통해 알려주기
        log.info("토큰 발급 및 대기열 진입 성공: " + token);
        log.info(getRank(token));
        return token;
    }

    public String getRank(String token) {
        if (!jwt.validateToken(token))
            return "유효하지 않은 토큰"; //예외처리 필요

        String key = jwt.getPerformanceId(token);

        Long queueSize = queueRepository.getQueueSize(key);
        Long userRank = queueRepository.getRank(token, key);

        return "총 대기자 수: " + queueSize + ", 현재 대기 순번: " + (userRank + 1);
    }

    public void processQueueBatch(int batchSize) {
        Set<String> performanceList = queueRepository.getActivePerformanceIds();

        for (String performance : performanceList) {
            List<String> users = queueRepository.getTopUsers(performance, batchSize);
            for (String token : users) {
                //카프카 이벤트 전송
                sendEvent(token);
                log.info("카프카 이벤트 전송 성공. 공연 UUID: " + performance);
            }

            queueRepository.removeTopUsers(performance, batchSize);

            //해당 공연의 대기자 수가 0명이면 공연 관리 set에서 공연 삭제 & 해당 공연 대기열 set 삭제
            if(users.size() <= batchSize && queueRepository.getQueueSize(performance) == 0) {
                queueRepository.removeActivePerformance(performance);
                queueRepository.deleteUserSet(performance);
                queueRepository.deleteQueue(performance);

                log.info("대기자 없음. 공연 set에서 삭제: " + performance);
            }
        }
    }

    private void sendEvent(String token) {
        UUID userId = UUID.fromString(jwt.getUserId(token));
        UUID performanceId = UUID.fromString(jwt.getPerformanceId(token));
        UUID performanceScheduleId = UUID.fromString(jwt.getPerformanceScheduleId(token));

        kafkaProducer.sendBookingRequestEvent(new BookingRequestMessage(userId, performanceId, performanceScheduleId));
    }
}
