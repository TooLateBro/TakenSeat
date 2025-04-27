package com.taken_seat.performance_service.performancehall.application.dto.mapper;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallCreateResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallDetailResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallPageResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallSearchResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallSeatDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallUpdateResponseDto;

@Component
public class HallResponseMapper {

	public static HallCreateResponseDto createHallToDto(PerformanceHall performanceHall) {
		return new HallCreateResponseDto(
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

	public static HallSeatDto toSeat(Seat seat) {
		return new HallSeatDto(
			seat.getId(),
			seat.getRowNumber(),
			seat.getSeatNumber(),
			seat.getSeatType(),
			seat.getStatus()
		);
	}

	public static HallDetailResponseDto toDetail(PerformanceHall performanceHall) {
		return new HallDetailResponseDto(
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

	public HallPageResponseDto toPage(Page<HallSearchResponseDto> pages) {

		return new HallPageResponseDto(
			pages.getContent(),
			pages.getSize(),
			pages.getNumber(),
			pages.getTotalPages(),
			pages.getTotalElements(),
			pages.isLast()
		);
	}

	public static HallUpdateResponseDto toUpdate(PerformanceHall performanceHall) {
		return new HallUpdateResponseDto(
			performanceHall.getId(),
			performanceHall.getName(),
			performanceHall.getAddress(),
			performanceHall.getTotalSeats(),
			performanceHall.getDescription(),
			performanceHall.getSeats().stream()
				.map(seat -> new HallSeatDto(
					seat.getId(),
					seat.getRowNumber(),
					seat.getSeatNumber(),
					seat.getSeatType(),
					seat.getStatus()
				))
				.collect(Collectors.toList())
		);
	}
}