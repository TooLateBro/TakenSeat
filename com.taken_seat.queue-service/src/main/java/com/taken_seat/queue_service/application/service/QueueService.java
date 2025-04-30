package com.taken_seat.queue_service.application.service;

import com.taken_seat.common_service.exception.customException.QueueException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.QueueEnterMessage;
import com.taken_seat.queue_service.application.dto.QueueReqDto;
import com.taken_seat.queue_service.application.dto.TokenReqDto;
import com.taken_seat.queue_service.application.dto.TokenResDto;
import com.taken_seat.queue_service.infrastructure.jwt.JwtImpl;
import com.taken_seat.queue_service.infrastructure.messaging.QueueKafkaProducerImpl;
import com.taken_seat.queue_service.infrastructure.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public TokenResDto enterQueue(QueueReqDto reqDto, UUID userID) {
        try{
            String token = jwt.createAccessToken(userID, reqDto.getPerformanceId(), reqDto.getPerformanceScheduleId());
            //각 공연마다 대기열을 만들어 관리하기 위해 공연 UUID를 Key로 설정
            String key = reqDto.getPerformanceId().toString();
            //timestamp를 통해 대기 순서 보장
            long timestamp = System.currentTimeMillis();

            //기존에 존재하는 토큰이라면(재입장 시) -> 기존 큐 및 관리 Set에서 삭제 후 대기열 맨 뒤로 보내기
            if (queueRepository.setIsMember(token)) {
                queueRepository.exitQueue(token, key);
                queueRepository.removeUser(token);
            }

            // 토큰 재/신규 추가
            queueRepository.enterQueue(token, key, timestamp);
            queueRepository.addUser(token);

            //공연이 공연 관리 set에 존재하지 않는다면 넣어주기
            if(!queueRepository.setIsPerformance(key)) {
                queueRepository.addActivePerformance(key);
            }

            //이 토큰을 프론트에서 지니고 있다가 유저 랭크 조회 시 해당 토큰을 통해 알려주기
            log.info("토큰 발급 및 대기열 진입 성공: " + token);
            log.info(getRank(new TokenReqDto(token)));
            return new TokenResDto(token);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getRank(TokenReqDto reqDto) {
        try {
            String token = reqDto.getToken();

            if (!jwt.validateToken(token))
                throw new QueueException(ResponseCode.QUEUE_UNAUTHORIZED_TOKEN_EXCEPTION);

            String key = jwt.getPerformanceId(token);

            //대기열에 없는 사용자일 때
            if(!queueRepository.setIsMember(token))
                throw new QueueException(ResponseCode.QUEUE_NOT_FOUND_TOKEN_EXCEPTION);

            Long queueSize = queueRepository.getQueueSize(key);
            Long userRank = queueRepository.getRank(token, key);

            return "총 대기자 수: " + queueSize + ", 현재 대기 순번: " + (userRank + 1);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

public void processQueueBatch(int batchSize) {
        try {
            Set<String> performanceList = queueRepository.getActivePerformanceIds();

            for (String performance : performanceList) {
                redisSendEvent(performance, batchSize);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void sendToBooking(QueueEnterMessage message) {
        try {
            String performance = message.getPerformanceId().toString();
            redisSendEvent(performance, 1);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void performanceSetBatch() {
        try {
            Set<String> performanceList = queueRepository.getActivePerformanceIds();

            for (String performance : performanceList) {
                //해당 공연의 대기자 수가 0명이면 공연 관리 set에서 공연 삭제 & 해당 공연 대기열 set 삭제
                if(queueRepository.getQueueSize(performance) == 0) {
                    queueRepository.removeActivePerformance(performance);
                    queueRepository.deleteQueue(performance);

                    log.info("대기자 없음. 공연 set에서 삭제: " + performance);
                }
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void redisSendEvent(String performance, int batchSize) {
        List<String> users = queueRepository.getTopUsers(performance, batchSize);
        for (String token : users) {
            //카프카 이벤트 전송
            kafkaSendEvent(token);
            //대기열 인원 관리 set에서 유저 삭제
            queueRepository.removeUser(token);
            log.info("카프카 이벤트 전송 성공. 공연 UUID: " + performance);
        }
        //Sorted Set에서 인원 삭제
        queueRepository.removeTopUsers(performance, batchSize);
    }

    private void kafkaSendEvent(String token) {
        UUID userId = UUID.fromString(jwt.getUserId(token));
        UUID performanceId = UUID.fromString(jwt.getPerformanceId(token));
        UUID performanceScheduleId = UUID.fromString(jwt.getPerformanceScheduleId(token));

        kafkaProducer.sendBookingRequestEvent(new BookingRequestMessage(userId, performanceId, performanceScheduleId));
    }
}
