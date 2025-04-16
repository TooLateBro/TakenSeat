package com.taken_seat.queue_service.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class QueueRepository {
    private static final String QUEUE_KEY = "toolatebro:";
    private static final String USER_NAMESPACE = "toolatebro:activeToken:";
    private static final String PERFORMANCE_NAMESPACE = "performance:active";

    private final RedisTemplate<String, String> redisTemplate;

    //Sorted Set
    public void enterQueue(String token, String performanceId, long timestamp) {
        redisTemplate.opsForZSet().add(QUEUE_KEY + performanceId, token, timestamp);
    }

    //사용자의 현재 대기 순번 조회
    public Long getRank(String token, String performanceId) {
        return redisTemplate.opsForZSet().rank(QUEUE_KEY + performanceId, token);
    }

    public boolean exitQueue(String token, String performanceId) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().remove(QUEUE_KEY + performanceId, token));
    }

    public List<String> getTopUsers(String performanceId, int count) {
        Set<String> range = redisTemplate.opsForZSet().range(QUEUE_KEY + performanceId, 0, count - 1);
        return range == null ? Collections.emptyList() : new ArrayList<>(range);
    }

    public void removeTopUsers(String performanceId, int count) {
        // 대기열에서 0 ~ count-1 순위까지 bulk 삭제
        redisTemplate.opsForZSet().removeRange(QUEUE_KEY + performanceId, 0, count - 1);
    }

    //공연 전체 대기자 수 구하기
    public Long getQueueSize(String performanceId) {
        return redisTemplate.opsForZSet().size(QUEUE_KEY + performanceId);
    }

    public void deleteQueue(String performanceId) {
        redisTemplate.delete(QUEUE_KEY + performanceId); // Sorted Set 삭제
    }


    //Set
    public Long addUser(String performanceId, String token) {
        return redisTemplate.opsForSet().add(USER_NAMESPACE + performanceId, token);
    }

    //유저가 이미 대기열에 있는지 확인
    public Boolean setIsMember(String performanceId, String token) {
        return redisTemplate.opsForSet().isMember(USER_NAMESPACE + performanceId, token);
    }

    public void deleteUserSet(String performanceId) {
        redisTemplate.delete(USER_NAMESPACE + performanceId);
    }

    //공연Id 관리
    public Long addActivePerformance(String performanceId) {
        return redisTemplate.opsForSet().add(PERFORMANCE_NAMESPACE, performanceId);
    }

    public Boolean setIsPerformance(String performanceId) {
        return redisTemplate.opsForSet().isMember(PERFORMANCE_NAMESPACE, performanceId);
    }

    public Set<String> getActivePerformanceIds() {
        return redisTemplate.opsForSet().members(PERFORMANCE_NAMESPACE);
    }

    public void removeActivePerformance(String performanceId) {
        redisTemplate.opsForSet().remove(PERFORMANCE_NAMESPACE, performanceId);
    }
}