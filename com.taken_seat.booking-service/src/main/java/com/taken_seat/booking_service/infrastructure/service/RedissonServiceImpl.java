package com.taken_seat.booking_service.infrastructure.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.application.RedissonService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedissonServiceImpl implements RedissonService {

	private final RedissonClient redissonClient;

	@Override
	public void tryHoldSeat(UUID userId, UUID seatId) {
		String key = "lock:seat:" + seatId;
		RLock lock = redissonClient.getLock(key);

		try {
			if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {
				// TODO: 좌석 선점 요청 보내기
				// TODO: 공연 도메인에게 해당 좌석이 유효한지, 이미 선점된 좌석인지 확인 부탁하기
			} else {
				throw new RuntimeException("이미 선점된 좌석입니다.");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("락 대기 중 인터럽트 발생", e);
		}
	}
}