package com.taken_seat.booking_service.booking.application;

import java.util.UUID;

public interface RedissonService {
	void tryHoldSeat(UUID performanceId, UUID performanceScheduleId, UUID seatId);
}