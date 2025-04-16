package com.taken_seat.queue_service.application.service;

import com.taken_seat.queue_service.application.dto.QueueReqDto;
import com.taken_seat.queue_service.infrastructure.jwt.JwtImpl;
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
        return token;
    }

    public String getRank(String token) {
        if (!jwt.validateToken(token))
            return "대기열에 존재 x";

        String key = jwt.getPerformanceId(token);

        Long queueSize = queueRepository.getQueueSize(key);
        Long userRank = queueRepository.getRank(token, key);

        return "총 대기자 수: " + queueSize + ", 현재 대기 순번: " + userRank;
    }

}
