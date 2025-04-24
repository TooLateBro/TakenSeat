package com.taken_seat.performance_service.performancehall.application.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceHallRedissonService {

	private final RedissonClient redissonClient;
	private final PerformanceHallClientService performanceHallClientService;

	@Value("${variable.lock-wait-time}")
	private long LOCK_WAIT_TIME;
	@Value("${variable.lock-lease-time}")
	private long LOCK_LEASE_TIME;

	public BookingSeatClientResponseDto updateSeatStatusWithLock(BookingSeatClientRequestDto request) {
		return executeWithLock(request.getSeatId(), () -> performanceHallClientService.updateSeatStatus(request));
	}

	public BookingSeatClientResponseDto updateSeatStatusCancelWithLock(BookingSeatClientRequestDto request) {
		return executeWithLock(request.getSeatId(), () -> performanceHallClientService.cancelSeatStatus(request));
	}

	private BookingSeatClientResponseDto executeWithLock(UUID seatId, Supplier<BookingSeatClientResponseDto> supplier) {
		String key = "lock:seat:" + seatId;
		RLock lock = redissonClient.getLock(key);
		boolean isLocked = false;

		try {
			isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
			log.info("[Performance] 락 설정 확인 - LOCK_WAIT_TIME = {}초, LOCK_LEASE_TIME = {}초", LOCK_WAIT_TIME,
				LOCK_LEASE_TIME);
			if (!isLocked) {
				log.warn("[Performance] 락 획득 실패 - seatId={}", seatId);
				throw new PerformanceException(ResponseCode.SEAT_LOCK_FAILED);
			}
			return supplier.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new PerformanceException(ResponseCode.SEAT_INTERRUPTED_EXCEPTION);
		} finally {
			if (isLocked && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}
