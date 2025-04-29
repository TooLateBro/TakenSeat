package com.taken_seat.performance_service.performance.application.helper;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.domain.repository.redis.SeatStatusRedisRepository;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatStatusRedisHelper {

	private final SeatStatusRedisRepository seatStatusRedisRepository;

	public void saveSeatStatus(UUID performanceScheduleId, UUID scheduleSeatId, SeatStatus seatStatus) {

		seatStatusRedisRepository.saveSeatStatus(performanceScheduleId, scheduleSeatId, seatStatus.name());
	}

	public SeatStatus getSeatStatus(UUID performanceScheduleId, UUID scheduleSeatId) {

		String status = seatStatusRedisRepository.getSeatStatus(performanceScheduleId, scheduleSeatId);
		if (status == null) {
			throw new PerformanceException(ResponseCode.SEAT_STATUS_NOT_FOUND);
		}
		return SeatStatus.valueOf(status);
	}

	public Map<UUID, SeatStatus> getAllSeatStatuses(UUID performanceScheduleId) {

		Map<Object, Object> entries = seatStatusRedisRepository.getAllSeatStatus(performanceScheduleId);
		return entries.entrySet().stream()
			.collect(Collectors.toMap(
				entry -> UUID.fromString(entry.getKey().toString()),
				entry -> SeatStatus.valueOf(entry.getValue().toString())
			));
	}
}

