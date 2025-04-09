package com.taken_seat.booking_service.application;

import java.util.UUID;

public interface RedissonService {
	void tryHoldSeat(UUID userId, UUID seatId);
}