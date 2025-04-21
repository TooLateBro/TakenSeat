package com.taken_seat.booking_service.booking.application.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeatLayoutResponseDto {

	private List<SeatDto> seats;

	@Getter
	@NoArgsConstructor
	public class SeatDto {
		private UUID seatId;
		private String rowNumber;
		private String seatNumber;
		private SeatType seatType;
		private SeatStatus status;

		public enum SeatType {
			VIP, R, S, A
		}

		public enum SeatStatus {
			AVAILABLE, SOLDOUT, DISABLED
		}
	}
}