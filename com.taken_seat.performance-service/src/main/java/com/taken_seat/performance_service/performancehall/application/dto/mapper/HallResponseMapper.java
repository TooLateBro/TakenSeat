package com.taken_seat.performance_service.performancehall.application.dto.mapper;

import java.util.stream.Collectors;

import com.taken_seat.performance_service.performancehall.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.SeatDto;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;

public class HallResponseMapper {

	public static CreateResponseDto createHallToDto(PerformanceHall performanceHall) {
		return CreateResponseDto.builder()
			.performanceHallId(performanceHall.getId())
			.name(performanceHall.getName())
			.address(performanceHall.getAddress())
			.totalSeats(performanceHall.getTotalSeats())
			.description(performanceHall.getDescription())
			.seats(
				performanceHall.getSeats().stream()
					.map(HallResponseMapper::toSeat)
					.collect(Collectors.toList())
			)
			.build();
	}

	public static SeatDto toSeat(Seat seat) {
		return SeatDto.builder()
			.seatId(seat.getId())
			.performanceHallId(seat.getPerformanceHall().getId())
			.seatNumber(seat.getSeatNumber())
			.rowNumber(seat.getRowNumber())
			.seatType(seat.getSeatType())
			.status(seat.getStatus())
			.build();
	}

	public static DetailResponseDto toDetail(PerformanceHall performanceHall) {
		return DetailResponseDto.builder()
			.performanceHallId(performanceHall.getId())
			.name(performanceHall.getName())
			.address(performanceHall.getAddress())
			.totalSeats(performanceHall.getTotalSeats())
			.description(performanceHall.getDescription())
			.seats(
				performanceHall.getSeats().stream()
					.map(HallResponseMapper::toSeat)
					.collect(Collectors.toList())
			)
			.build();
	}
}
