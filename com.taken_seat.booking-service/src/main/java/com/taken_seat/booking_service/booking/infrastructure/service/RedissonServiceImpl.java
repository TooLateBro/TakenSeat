package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.application.service.RedissonService;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedissonServiceImpl implements RedissonService {

	private final RedissonClient redissonClient;
	private final BookingClientService bookingClientService;

	@Override
	public BookingSeatClientResponseDto tryHoldSeat(UUID performanceId, UUID performanceScheduleId, UUID seatId) {
		String key = "lock:seat:" + seatId;
		RLock lock = redissonClient.getLock(key);

		// TODO: FeignClient 처리에서 Kafka 이벤트 처리로 변경해보기
		try {
			if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {
				BookingSeatClientRequestDto dto = BookingSeatClientRequestDto.builder()
					.performanceId(performanceId)
					.performanceScheduleId(performanceScheduleId)
					.seatId(seatId)
					.build();

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