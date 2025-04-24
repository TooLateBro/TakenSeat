package com.taken_seat.performance_service.performancehall.application.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.SearchResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.SeatDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.UpdateResponseDto;

@Component
public class HallResponseMapper {

	public static CreateResponseDto createHallToDto(PerformanceHall performanceHall) {
		return new CreateResponseDto(
			performanceHall.getId(),
			performanceHall.getName(),
			performanceHall.getAddress(),
			performanceHall.getTotalSeats(),
			performanceHall.getDescription(),
			performanceHall.getSeats().stream()
				.map(HallResponseMapper::toSeat)
				.collect(Collectors.toList())
		);
	}

	public static SeatDto toSeat(Seat seat) {
		return new SeatDto(
			seat.getId(),
			seat.getRowNumber(),
			seat.getSeatNumber(),
			seat.getSeatType(),
			seat.getStatus()
		);
	}

	public static DetailResponseDto toDetail(PerformanceHall performanceHall) {
		return new DetailResponseDto(
			performanceHall.getId(),
			performanceHall.getName(),
			performanceHall.getAddress(),
			performanceHall.getTotalSeats(),
			performanceHall.getDescription(),
			performanceHall.getSeats().stream()
				.map(HallResponseMapper::toSeat)
				.collect(Collectors.toList())
		);
	}

	public PageResponseDto toPage(Page<SearchResponseDto> pages) {

		return new PageResponseDto(
			pages.getContent(),
			pages.getSize(),
			pages.getNumber(),
			pages.getTotalPages(),
			pages.getTotalElements(),
			pages.isLast()
		);
	}

	public static UpdateResponseDto toUpdate(PerformanceHall performanceHall) {
		return new UpdateResponseDto(
			performanceHall.getId(),
			performanceHall.getName(),
			performanceHall.getAddress(),
			performanceHall.getTotalSeats(),
			performanceHall.getDescription(),
			performanceHall.getSeats().stream()
				.map(seat -> new SeatDto(
					seat.getId(),
					seat.getRowNumber(),
					seat.getSeatNumber(),
					seat.getSeatType(),
					seat.getStatus()
				))
				.collect(Collectors.toList())
		);
	}

	public static List<SeatDto> toSeatLayout(List<Seat> seats) {
		return seats.stream()
			.map(HallResponseMapper::toSeat)
			.collect(Collectors.toList());
	}
}