package com.taken_seat.performance_service.performancehall.application.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performancehall.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.SearchResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.SeatDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.UpdateResponseDto;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;

@Component
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

	public SearchResponseDto toSearch(PerformanceHall performanceHall) {
		return SearchResponseDto.builder()
			.performanceHallId(performanceHall.getId())
			.name(performanceHall.getName())
			.totalSeats(performanceHall.getTotalSeats())
			.build();
	}

	public List<SearchResponseDto> toSearchList(List<PerformanceHall> performanceHalls) {
		return performanceHalls.stream()
			.map(this::toSearch)
			.collect(Collectors.toList());
	}

	public PageResponseDto toPage(Page<PerformanceHall> pages) {

		List<SearchResponseDto> content = toSearchList(pages.getContent());
		return PageResponseDto.builder()
			.content(content)
			.pageSize(pages.getSize())
			.pageNumber(pages.getNumber() + 1)
			.totalPages(pages.getTotalPages())
			.totalElements(pages.getTotalElements())
			.isLast(pages.isLast())
			.build();
	}

	public static UpdateResponseDto toUpdate(PerformanceHall performanceHall) {
		return UpdateResponseDto.builder()
			.performanceHallId(performanceHall.getId())
			.name(performanceHall.getName())
			.address(performanceHall.getAddress())
			.totalSeats(performanceHall.getTotalSeats())
			.description(performanceHall.getDescription())
			.seats(
				performanceHall.getSeats().stream()
					.map(seat -> SeatDto.builder()
						.rowNumber(seat.getRowNumber())
						.seatNumber(seat.getSeatNumber())
						.seatType(seat.getSeatType())
						.status(seat.getStatus())
						.build())
					.toList()
			)
			.build();
	}
}