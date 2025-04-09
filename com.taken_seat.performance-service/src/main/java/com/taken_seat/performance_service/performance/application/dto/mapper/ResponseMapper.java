package com.taken_seat.performance_service.performance.application.dto.mapper;

import java.util.stream.Collectors;

import com.taken_seat.performance_service.performance.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.PerformanceScheduleResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.SeatPriceResponseDto;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSeatPrice;

public class ResponseMapper {

	public static CreateResponseDto createToDto(Performance performance) {
		return CreateResponseDto.builder()
			.performanceId(performance.getId())
			.title(performance.getTitle())
			.description(performance.getDescription())
			.startAt(performance.getStartAt())
			.endAt(performance.getEndAt())
			.status(performance.getStatus())
			.posterUrl(performance.getPosterUrl())
			.ageLimit(performance.getAgeLimit())
			.maxTicketCount(performance.getMaxTicketCount())
			.discountInfo(performance.getDiscountInfo())
			.schedules(
				performance.getSchedules().stream()
					.map(ResponseMapper::toScheduleDto)
					.collect(Collectors.toList())
			)
			.build();
	}

	private static PerformanceScheduleResponseDto toScheduleDto(PerformanceSchedule schedule) {
		return PerformanceScheduleResponseDto.builder()
			.performanceScheduleId(schedule.getId())
			.performanceHallId(schedule.getPerformanceHallId())
			.startAt(schedule.getStartAt())
			.endAt(schedule.getEndAt())
			.saleStartAt(schedule.getSaleStartAt())
			.saleEndAt(schedule.getSaleEndAt())
			.status(schedule.getStatus())
			.seatPrices(
				schedule.getSeatPrices().stream()
					.map(ResponseMapper::toSeatPriceDto)
					.collect(Collectors.toList())
			)
			.build();
	}

	private static SeatPriceResponseDto toSeatPriceDto(PerformanceSeatPrice seatPrice) {
		return SeatPriceResponseDto.builder()
			.performanceSeatPriceId(seatPrice.getId())
			.seatType(seatPrice.getSeatType())
			.price(seatPrice.getPrice())
			.build();
	}

	public static DetailResponseDto detailToDto(Performance performance) {
		return DetailResponseDto.builder()
			.performanceId(performance.getId())
			.title(performance.getTitle())
			.description(performance.getDescription())
			.startAt(performance.getStartAt())
			.endAt(performance.getEndAt())
			.status(performance.getStatus())
			.posterUrl(performance.getPosterUrl())
			.ageLimit(performance.getAgeLimit())
			.maxTicketCount(performance.getMaxTicketCount())
			.discountInfo(performance.getDiscountInfo())
			.schedules(
				performance.getSchedules().stream()
					.map(ResponseMapper::toScheduleDto)
					.collect(Collectors.toList())
			)
			.build();
	}
}
