package com.taken_seat.queue_service.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

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
        Set<String> range = redisTemplate.opsForZSet().range(QUEUE_KEY + performanceId, 0, count > 0 ? count - 1 : 0);
        if (range == null || range.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(range);
    }

    public void removeTopUsers(String performanceId, int count) {
        if (count <= 0)
            return; // 0 이하면 삭제할 필요 없음

        // 0 ~ (count - 1)까지 순위 삭제
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
    public Long addUser(String token) {
        return redisTemplate.opsForSet().add(USER_NAMESPACE, token);
    }

    //유저가 이미 대기열에 있는지 확인
    public Boolean setIsMember(String token) {
        return redisTemplate.opsForSet().isMember(USER_NAMESPACE, token);
    }

    public void removeUser(String token) {
        redisTemplate.opsForSet().remove(USER_NAMESPACE, token);
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