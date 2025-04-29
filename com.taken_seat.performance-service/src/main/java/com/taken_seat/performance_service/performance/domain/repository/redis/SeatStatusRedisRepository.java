package com.taken_seat.performance_service.performance.domain.repository.redis;

import java.util.Map;
import java.util.UUID;

public interface SeatStatusRedisRepository {

	void saveSeatStatus(
		UUID performanceScheduleId,
		UUID scheduleSeatId,
		String seatStatus);

	String getSeatStatus(
		UUID performanceScheduleId,
		UUID scheduleSeatId
	);

	Map<Object, Object> getAllSeatStatus(UUID performanceScheduleId);

	void deleteSeatStatus(
		UUID performanceScheduleId,
		UUID scheduleSeatId);

	void deleteAllSeatStatus(UUID performanceScheduleId);
}
