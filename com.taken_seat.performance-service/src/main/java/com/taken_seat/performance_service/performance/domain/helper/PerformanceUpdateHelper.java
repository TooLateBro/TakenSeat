package com.taken_seat.performance_service.performance.domain.helper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceScheduleCommand;
import com.taken_seat.performance_service.performance.application.dto.command.UpdateSeatPriceCommand;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSeatPrice;
import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;
import com.taken_seat.performance_service.performancehall.domain.facade.PerformanceHallFacade;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PerformanceUpdateHelper {

	public static void updateSchedules(Performance performance, List<UpdatePerformanceScheduleCommand> scheduleCommand,
		UUID updatedBy) {

		for (UpdatePerformanceScheduleCommand command : scheduleCommand) {
			Optional<PerformanceSchedule> matched = performance.getSchedules().stream()
				.filter(s -> s.getId().equals(command.performanceScheduleId()))
				.findFirst();

			if (matched.isPresent()) {
				updateSchedule(matched.get(), command, updatedBy);
			} else {
				PerformanceSchedule newSchedule = createNewSchedule(command, performance, updatedBy);
				performance.getSchedules().add(newSchedule);
			}
		}
	}

	private static void updateSchedule(PerformanceSchedule schedule, UpdatePerformanceScheduleCommand command,
		UUID updatedBy) {

		schedule.preUpdate(updatedBy);
		schedule.update(command);

		updateSeatPrices(schedule, command.seatPrices(), updatedBy);
	}

	private static PerformanceSchedule createNewSchedule(UpdatePerformanceScheduleCommand command,
		Performance performance,
		UUID updatedBy) {

		PerformanceSchedule schedule = PerformanceSchedule.builder()
			.performance(performance)
			.performanceHallId(command.performanceHallId())
			.startAt(command.startAt())
			.endAt(command.endAt())
			.saleStartAt(command.saleStartAt())
			.saleEndAt(command.saleEndAt())
			.status(command.status())
			.build();

		schedule.prePersist(updatedBy);
		updateSeatPrices(schedule, command.seatPrices(), updatedBy);
		return schedule;
	}

	private static void updateSeatPrices(PerformanceSchedule schedule, List<UpdateSeatPriceCommand> seatPrices,
		UUID updatedBy) {

		if (seatPrices == null)
			return;

		for (UpdateSeatPriceCommand seatPrice : seatPrices) {
			Optional<PerformanceSeatPrice> matched = schedule.getSeatPrices().stream()
				.filter(p -> p.getId().equals(seatPrice.performanceSeatPriceId()))
				.findFirst();

			if (matched.isPresent()) {
				matched.get().update(seatPrice);
			} else {
				PerformanceSeatPrice newPrice = PerformanceSeatPrice.builder()
					.performanceSchedule(schedule)
					.seatType(seatPrice.seatType())
					.price(seatPrice.price())
					.build();
				newPrice.prePersist(updatedBy);
				schedule.getSeatPrices().add(newPrice);
			}
		}
	}

	public static void updateStatus(Performance performance, AuthenticatedUser authenticatedUser,
		PerformanceHallFacade performanceHallFacade) {

		PerformanceStatus oldPerformanceStatus = performance.getStatus();
		PerformanceStatus newPerformanceStatus = PerformanceStatus.status(
			performance.getStartAt(),
			performance.getEndAt(),
			performance.getSchedules()
		);

		if (!performance.getStatus().equals(newPerformanceStatus)) {
			performance.updateStatus(newPerformanceStatus);

			log.info("[Performance] 공연 상태 변경 - 성공 - performanceId={}, oldStatus={}, newStatus={}, 변경자={}",
				performance.getId(),
				oldPerformanceStatus,
				newPerformanceStatus,
				authenticatedUser.getUserId());
		}

		for (PerformanceSchedule schedule : performance.getSchedules()) {

			UUID performanceHallId = schedule.getPerformanceHallId();

			boolean isSoldOut = performanceHallFacade.isSoldOut(performanceHallId);

			PerformanceScheduleStatus oldScheduleStatus = schedule.getStatus();
			PerformanceScheduleStatus newPerformanceScheduleStatus =
				PerformanceScheduleStatus.status(schedule.getSaleStartAt(), schedule.getSaleEndAt(), isSoldOut);

			if (!schedule.getStatus().equals(newPerformanceScheduleStatus)) {
				schedule.updateStatus(newPerformanceScheduleStatus);
				schedule.preUpdate(authenticatedUser.getUserId());

				log.info("[Performance] 회차 상태 변경 - 성공 - 공연회차 ID={}, oldStatus={}, newStatus={}, 공연 ID={}, 변경자={}",
					schedule.getId(),
					oldScheduleStatus,
					newPerformanceScheduleStatus,
					performance.getId(),
					authenticatedUser.getUserId());
			}
		}
	}
}

