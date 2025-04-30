package com.taken_seat.booking_service.booking.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record SeatLayoutResponseDto(
	List<ScheduleSeatResponseDto> scheduleSeats
) {
	public record ScheduleSeatResponseDto(
		UUID scheduleSeatId,
		String rowNumber,
		String seatNumber,
		SeatType seatType,
		SeatStatus seatStatus,
		Integer price
	) {
		public enum SeatType {
			VIP, R, S, A
		}

		public enum SeatStatus {
			AVAILABLE, SOLDOUT, DISABLED
		}
	}
}