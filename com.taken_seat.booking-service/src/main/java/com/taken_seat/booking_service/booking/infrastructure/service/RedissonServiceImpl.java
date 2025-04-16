package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		log.info("[Booking] 좌석 락 획득 시도 - seatId={}, scheduleId={}", seatId, performanceScheduleId);
		if (lock.tryLock()) {
			try {
				BookingSeatClientRequestDto dto = BookingSeatClientRequestDto.builder()
					.performanceId(performanceId)
					.performanceScheduleId(performanceScheduleId)
					.seatId(seatId)
					.build();

				BookingSeatClientResponseDto responseDto = bookingClientService.updateSeatStatus(dto);

				if (!responseDto.reserved()) {
					log.warn("[Booking] 좌석 선점 실패 - seatId={}, 이유=이미 예약됨", seatId);
					throw new BookingException(ResponseCode.BOOKING_SEAT_RESERVED_EXCEPTION);
				}
				log.info("[Booking] 좌석 선점 성공 - seatId={}", seatId);
				return responseDto;
			} finally {
				if (lock.isHeldByCurrentThread()) {
					lock.unlock();
					log.info("[Booking] 좌석 락 해제 - seatId={}", seatId);
				}
			}
		} else {
			throw new BookingException(ResponseCode.BOOKING_SEAT_LOCKED_EXCEPTION);
		}
	}
}