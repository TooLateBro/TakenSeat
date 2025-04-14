package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;

import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;

public interface RedissonService {
	BookingSeatClientResponseDto tryHoldSeat(UUID performanceId, UUID performanceScheduleId, UUID seatId);
}