package com.taken_seat.performance_service.performance.domain.helper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performance.application.dto.command.CreatePerformanceCommand;
import com.taken_seat.performance_service.performance.application.dto.command.CreatePerformanceScheduleCommand;
import com.taken_seat.performance_service.performance.application.dto.command.CreateSeatPriceCommand;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;
import com.taken_seat.performance_service.performance.domain.model.ScheduleSeat;
import com.taken_seat.performance_service.performancehall.application.dto.command.SeatTemplateInfo;
import com.taken_seat.performance_service.performancehall.domain.facade.PerformanceHallFacade;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PerformanceCreateHelper {

	private final PerformanceHallFacade performanceHallFacade;

	public Performance createPerformance(CreatePerformanceCommand command, UUID createdBy) {
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

	public List<PerformanceSchedule> createPerformanceSchedules(
		List<CreatePerformanceScheduleCommand> commands,
		Performance performance,
		UUID createdBy
	) {
		return commands.stream()
			.map(command -> {
				PerformanceSchedule schedule = createPerformanceSchedule(command, performance);
				schedule.prePersist(createdBy);

				Map<SeatType, Integer> seatPriceMap = command.seatPrices().stream()
					.collect(Collectors.toMap(
						CreateSeatPriceCommand::seatType,
						CreateSeatPriceCommand::price
					));

				List<SeatTemplateInfo> seatTemplates =
					performanceHallFacade.getSeatTemplate(command.performanceHallId());

				List<ScheduleSeat> scheduleSeats =
					createScheduleSeats(seatTemplates, schedule, seatPriceMap);

				schedule.addSeats(scheduleSeats);

				return schedule;
			})
			.toList();
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
