package com.taken_seat.performance_service.performance.application.dto.mapper;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.ScheduleSeat;
import com.taken_seat.performance_service.performance.presentation.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.PerformanceScheduleResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.ScheduleSeatResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.SearchResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.UpdateResponseDto;

@Component
public class PerformanceResponseMapper {
	public static CreateResponseDto createToDto(Performance performance) {
		return new CreateResponseDto(
			performance.getId(),
			performance.getTitle(),
			performance.getDescription(),
			performance.getStartAt(),
			performance.getEndAt(),
			performance.getStatus(),
			performance.getPosterUrl(),
			performance.getAgeLimit(),
			performance.getMaxTicketCount(),
			performance.getDiscountInfo(),
			performance.getSchedules().stream()
				.map(PerformanceResponseMapper::toScheduleDto)
				.collect(Collectors.toList())
		);
	}

	private static PerformanceScheduleResponseDto toScheduleDto(PerformanceSchedule schedule) {
		return new PerformanceScheduleResponseDto(
			schedule.getId(),
			schedule.getPerformanceHallId(),
			schedule.getStartAt(),
			schedule.getEndAt(),
			schedule.getSaleStartAt(),
			schedule.getSaleEndAt(),
			schedule.getStatus(),
			schedule.getScheduleSeats().stream()
				.map(PerformanceResponseMapper::toScheduleSeatDto)
				.collect(Collectors.toList())
		);
	}

	private static ScheduleSeatResponseDto toScheduleSeatDto(ScheduleSeat scheduleSeat) {
		return new ScheduleSeatResponseDto(
			scheduleSeat.getId(),
			scheduleSeat.getRowNumber(),
			scheduleSeat.getSeatNumber(),
			scheduleSeat.getSeatType(),
			scheduleSeat.getSeatStatus(),
			scheduleSeat.getPrice()
		);
	}

	public static DetailResponseDto detailToDto(Performance performance) {
		return new DetailResponseDto(
			performance.getId(),
			performance.getTitle(),
			performance.getDescription(),
			performance.getStartAt(),
			performance.getEndAt(),
			performance.getStatus(),
			performance.getPosterUrl(),
			performance.getAgeLimit(),
			performance.getMaxTicketCount(),
			performance.getDiscountInfo(),
			performance.getSchedules().stream()
				.map(PerformanceResponseMapper::toScheduleDto)
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

	public static UpdateResponseDto toUpdate(Performance performance) {
		return new UpdateResponseDto(
			performance.getId(),
			performance.getTitle(),
			performance.getDescription(),
			performance.getStartAt(),
			performance.getEndAt(),
			performance.getStatus(),
			performance.getPosterUrl(),
			performance.getAgeLimit(),
			performance.getMaxTicketCount(),
			performance.getDiscountInfo(),
			performance.getSchedules().stream()
				.map(schedule -> new PerformanceScheduleResponseDto(
					schedule.getId(),
					schedule.getPerformanceHallId(),
					schedule.getStartAt(),
					schedule.getEndAt(),
					schedule.getSaleStartAt(),
					schedule.getSaleEndAt(),
					schedule.getStatus(),
					schedule.getSeatPrices().stream()
						.map(seatPrice -> new SeatPriceResponseDto(
							seatPrice.getId(),
							seatPrice.getSeatType(),
							seatPrice.getPrice()
						))
						.collect(Collectors.toList())
				))
				.collect(Collectors.toList())
		);
	}
}
