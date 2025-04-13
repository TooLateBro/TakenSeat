package com.taken_seat.performance_service.performancehall.domain.helper;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performancehall.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

public class PerformanceHallCreateHelper {

	public static PerformanceHall createPerformanceHall(CreateRequestDto request, UUID createBy) {
		PerformanceHall performanceHall = PerformanceHall.builder()
			.name(request.getName())
			.address(request.getAddress())
			.totalSeats(request.getTotalSeats())
			.description(request.getDescription())
			.build();

		performanceHall.prePersist(createBy);
		return performanceHall;
	}

	public static List<Seat> createSeats(CreateRequestDto request, PerformanceHall performanceHall, UUID createdBy) {
		List<Seat> seats = request.getSeats().stream()
			.map(createSeatDto -> {
				Seat seat = Seat.builder()
					.performanceHall(performanceHall)
					.rowNumber(createSeatDto.getRowNumber())
					.seatNumber(createSeatDto.getSeatNumber())
					.seatType(createSeatDto.getSeatType())
					.status(createSeatDto.getStatus() != null ? createSeatDto.getStatus() : SeatStatus.AVAILABLE)
					.build();
				seat.prePersist(createdBy);
				return seat;
			})
			.toList();

		performanceHall.getSeats().addAll(seats);
		return seats;
	}
}
