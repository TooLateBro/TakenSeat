package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedissonService {

	private final RedissonClient redissonClient;
	private final BookingClientService bookingClientService;
	@Value("${variable.lock-wait-time}")
	private long LOCK_WAIT_TIME;
	@Value("${variable.lock-lease-time}")
	private long LOCK_LEASE_TIME;

	public BookingSeatClientResponseDto tryHoldSeat(UUID performanceId, UUID performanceScheduleId,
		UUID scheduleSeatId) {
		String key = "lock:seat:" + scheduleSeatId;
		RLock lock = redissonClient.getLock(key);

		// TODO: FeignClient 처리에서 Kafka 이벤트 처리로 변경해보기
		try {
			if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
				BookingSeatClientRequestDto dto = new BookingSeatClientRequestDto(
					performanceId,
					performanceScheduleId,
					scheduleSeatId
				);

				BookingSeatClientResponseDto responseDto = bookingClientService.updateSeatStatus(dto);

				if (!responseDto.reserved()) {
					throw new BookingException(ResponseCode.BOOKING_SEAT_RESERVED_EXCEPTION);
				}
				return responseDto;
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