package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.booking.application.service.RedissonService;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedissonServiceImpl implements RedissonService {

	private final RedissonClient redissonClient;

	@Override
	public void tryHoldSeat(UUID performanceId, UUID performanceScheduleId, UUID seatId) {
		String key = "lock:seat:" + seatId;
		RLock lock = redissonClient.getLock(key);

		try {
			if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {
				// TODO: 좌석 선점 요청 보내기
				// TODO: 공연 도메인에게 해당 좌석이 유효한지, 이미 선점된 좌석인지 확인 부탁하기
			} else {
				throw new BookingException(ResponseCode.BOOKING_SEAT_LOCKED_EXCEPTION);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BookingException(ResponseCode.BOOKING_INTERRUPTED_EXCEPTION, e.getMessage());
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}