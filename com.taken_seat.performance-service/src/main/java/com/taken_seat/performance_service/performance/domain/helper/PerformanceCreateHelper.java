package com.taken_seat.performance_service.performance.domain.helper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.taken_seat.performance_service.performance.application.dto.command.CreatePerformanceCommand;
import com.taken_seat.performance_service.performance.application.dto.command.CreatePerformanceScheduleCommand;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSeatPrice;
import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;
import com.taken_seat.performance_service.performance.domain.model.ScheduleSeat;
import com.taken_seat.performance_service.performancehall.application.dto.command.SeatTemplateInfo;
import com.taken_seat.performance_service.performancehall.domain.facade.PerformanceHallFacade;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public class PerformanceCreateHelper {

	private final PerformanceHallFacade performanceHallFacade;

	public PerformanceCreateHelper(PerformanceHallFacade performanceHallFacade) {
		this.performanceHallFacade = performanceHallFacade;
	}

	public static Performance createPerformance(CreatePerformanceCommand command, UUID createdBy) {
		Performance performance = Performance.builder()
			.title(command.title())
			.description(command.description())
			.startAt(command.startAt())
			.endAt(command.endAt())
			.status(null)
			.posterUrl(command.posterUrl())
			.ageLimit(command.ageLimit())
			.maxTicketCount(command.maxTicketCount())
			.discountInfo(command.discountInfo())
			.build();

		performance.prePersist(createdBy);
		return performance;
	}

	public static void createPerformanceSchedules(CreatePerformanceCommand command,
		Performance performance, UUID createdBy) {

		List<PerformanceSchedule> schedules = command.schedules().stream()
			.map(createPerformanceScheduleCommand -> {
				PerformanceSchedule schedule = createPerformanceSchedule(createPerformanceScheduleCommand, performance);
				schedule.prePersist(createdBy);

				List<PerformanceSeatPrice> seatPrices = createPerformanceSeatPrices(createPerformanceScheduleCommand,
					schedule);
				schedule.getSeatPrices().addAll(seatPrices);

				seatPrices.forEach(seatPrice -> seatPrice.prePersist(createdBy));

				return schedule;
			})
			.toList();

		performance.getSchedules().addAll(schedules);

		PerformanceStatus status = PerformanceStatus.status(
			performance.getStartAt(),
			performance.getEndAt(),
			performance.getSchedules()
		);

		performance.updateStatus(status);

	}

	private static PerformanceSchedule createPerformanceSchedule(
		CreatePerformanceScheduleCommand command, Performance performance) {

		LocalDateTime saleStartAt = command.saleStartAt();
		LocalDateTime saleEndAt =
			command.saleEndAt() != null ? command.saleEndAt() : saleStartAt.minusMinutes(1);

		return PerformanceSchedule.builder()
			.performance(performance)
			.performanceHallId(command.performanceHallId())
			.startAt(command.startAt())
			.endAt(command.endAt())
			.saleStartAt(saleStartAt)
			.saleEndAt(saleEndAt)
			.status(PerformanceScheduleStatus.status(saleStartAt, saleEndAt, false))
			.build();
	}

	private static List<PerformanceSeatPrice> createPerformanceSeatPrices(
		CreatePerformanceScheduleCommand command, PerformanceSchedule schedule) {
		return command.seatPrices().stream()
			.map(seatPriceCommand -> PerformanceSeatPrice.builder()
				.performanceSchedule(schedule)
				.seatType(seatPriceCommand.seatType())
				.price(seatPriceCommand.price())
				.build())
			.toList();
	}

	private static List<ScheduleSeat> createScheduleSeats(
		List<SeatTemplateInfo> seatTemplates,
		PerformanceSchedule schedule,
		Map<SeatType, Integer> seatPrice
	) {
		return seatTemplates.stream()
			.map(seatTemplate -> ScheduleSeat.fromSeatTemplate(
				seatTemplate,
				schedule,
				seatPrice.getOrDefault(seatTemplate.seatType(), 0)
			))
			.toList();
	}
}
