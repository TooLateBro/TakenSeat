package com.taken_seat.performance_service.performance.domain.helper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceScheduleCommand;
import com.taken_seat.performance_service.performance.application.dto.command.UpdateScheduleSeatCommand;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;
import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

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

		updateScheduleSeats(schedule, command.seatPrices(), updatedBy);
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
		updateScheduleSeats(schedule, command.seatPrices(), updatedBy);
		return schedule;
	}

	private static void updateScheduleSeats(
		PerformanceSchedule schedule,
		List<UpdateScheduleSeatCommand> scheduleSeatCommands,
		UUID updatedBy
	) {

		if (scheduleSeatCommands == null)
			return;

		for (UpdateScheduleSeatCommand command : scheduleSeatCommands) {
			schedule.getScheduleSeats().stream()
				.filter(scheduleSeat -> scheduleSeat.getId().equals(command.scheduleSeatId()))
				.findFirst()
				.ifPresent(scheduleSeat -> {
					scheduleSeat.preUpdate(updatedBy);
					scheduleSeat.update(command);
				});
		}

	}

	public static void updateStatus(
		Performance performance,
		AuthenticatedUser authenticatedUser) {

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

			PerformanceScheduleStatus oldScheduleStatus = schedule.getStatus();
			PerformanceScheduleStatus newScheduleStatus =
				PerformanceScheduleStatus.status(
					schedule.getSaleStartAt(),
					schedule.getSaleEndAt(),
					schedule.isSoldOut());

			if (!schedule.getStatus().equals(newScheduleStatus)) {
				schedule.updateStatus(newScheduleStatus);
				schedule.preUpdate(authenticatedUser.getUserId());

				log.info("[Performance] 회차 상태 변경 - 성공 - 공연회차 ID={}, oldStatus={}, newStatus={}, 공연 ID={}, 변경자={}",
					schedule.getId(),
					oldScheduleStatus,
					newScheduleStatus,
					performance.getId(),
					authenticatedUser.getUserId());
			}
		}
	}
}

