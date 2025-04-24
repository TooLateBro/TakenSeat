package com.taken_seat.performance_service.performancehall.domain.helper;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performancehall.application.dto.command.CreatePerformanceHallCommand;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

public class PerformanceHallCreateHelper {

	public static PerformanceHall createPerformanceHall(CreatePerformanceHallCommand command, UUID createBy) {
		PerformanceHall performanceHall = PerformanceHall.builder()
			.name(command.name())
			.address(command.address())
			.totalSeats(command.totalSeats())
			.description(command.description())
			.build();

		performanceHall.prePersist(createBy);
		return performanceHall;
	}

	public static List<Seat> createSeats(CreatePerformanceHallCommand command, PerformanceHall performanceHall,
		UUID createdBy) {
		List<Seat> seats = command.seats().stream()
			.map(createSeatCommand -> {
				Seat seat = Seat.builder()
					.performanceHall(performanceHall)
					.rowNumber(createSeatCommand.rowNumber())
					.seatNumber(createSeatCommand.seatNumber())
					.seatType(createSeatCommand.seatType())
					.status(createSeatCommand.status() != null ? createSeatCommand.status() : SeatStatus.AVAILABLE)
					.build();
				seat.prePersist(createdBy);
				return seat;
			})
			.toList();

		performanceHall.getSeats().addAll(seats);
		return seats;
	}
}
