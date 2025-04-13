package com.taken_seat.performance_service.performance.domain.helper;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.application.dto.request.CreatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSeatPrice;
import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

public class PerformanceCreateHelper {

	// Performance 객체 생성
	public static Performance createPerformance(CreateRequestDto request, UUID createdBy) {
		Performance performance = Performance.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.startAt(request.getStartAt())
			.endAt(request.getEndAt())
			.status(PerformanceStatus.status(request.getStartAt(), request.getEndAt()))
			.posterUrl(request.getPosterUrl())
			.ageLimit(request.getAgeLimit())
			.maxTicketCount(request.getMaxTicketCount())
			.discountInfo(request.getDiscountInfo())
			.build();

		performance.prePersist(createdBy);
		return performance;
	}

	// PerformanceSchedule 리스트 생성
	public static List<PerformanceSchedule> createPerformanceSchedules(CreateRequestDto request,
		Performance performance, UUID createdBy) {

		List<PerformanceSchedule> schedules = request.getSchedules().stream()
			.map(createPerformanceScheduleDto -> {
				PerformanceSchedule schedule = createPerformanceSchedule(createPerformanceScheduleDto, performance);
				schedule.prePersist(createdBy);

				List<PerformanceSeatPrice> seatPrices = createPerformanceSeatPrices(createPerformanceScheduleDto,
					schedule);
				schedule.getSeatPrices().addAll(seatPrices);

				seatPrices.forEach(seatPriceCreatedBy -> seatPriceCreatedBy.prePersist(createdBy));

				return schedule;
			})
			.toList();
		performance.getSchedules().addAll(schedules);

		return schedules;
	}

	// PerformanceSchedule 객체 생성
	private static PerformanceSchedule createPerformanceSchedule(
		CreatePerformanceScheduleDto createPerformanceScheduleDto, Performance performance) {
		return PerformanceSchedule.builder()
			.performance(performance)
			.performanceHallId(createPerformanceScheduleDto.getPerformanceHallId())
			.startAt(createPerformanceScheduleDto.getStartAt())
			.endAt(createPerformanceScheduleDto.getEndAt())
			.saleStartAt(createPerformanceScheduleDto.getSaleStartAt())
			.saleEndAt(createPerformanceScheduleDto.getSaleEndAt())
			.status(PerformanceScheduleStatus.status(
				createPerformanceScheduleDto.getSaleStartAt(),
				createPerformanceScheduleDto.getSaleEndAt(),
				false
			))
			.build();
	}

	// PerformanceSeatPrice 리스트 생성
	private static List<PerformanceSeatPrice> createPerformanceSeatPrices(
		CreatePerformanceScheduleDto createPerformanceScheduleDto, PerformanceSchedule schedule) {
		return createPerformanceScheduleDto.getSeatPrices().stream()
			.map(CreateSeatPriceDto -> PerformanceSeatPrice.builder()
				.performanceSchedule(schedule)
				.seatType(CreateSeatPriceDto.getSeatType())
				.price(CreateSeatPriceDto.getPrice())
				.build())
			.toList();
	}
}
