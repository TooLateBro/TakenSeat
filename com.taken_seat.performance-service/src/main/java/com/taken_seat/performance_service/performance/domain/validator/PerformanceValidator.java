package com.taken_seat.performance_service.performance.domain.validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.application.dto.command.CreatePerformanceScheduleCommand;
import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceCommand;
import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceScheduleCommand;

public class PerformanceValidator {

	public static void validateDuplicateSchedules(List<CreatePerformanceScheduleCommand> schedules) {

		validateDuplicateScheduleLogic(schedules.stream()
			.map(dto -> new ScheduleDtoWrapper(dto.performanceHallId(), dto.startAt(), dto.endAt()))
			.toList());
	}

	public static void validateDuplicateSchedulesForUpdate(List<UpdatePerformanceScheduleCommand> schedules) {

		validateDuplicateScheduleLogic(schedules.stream()
			.map(dto -> new ScheduleDtoWrapper(dto.performanceHallId(), dto.startAt(), dto.endAt()))
			.toList());
	}

	private static void validateDuplicateScheduleLogic(List<ScheduleDtoWrapper> schedules) {

		Map<UUID, List<ScheduleDtoWrapper>> scheduleMap = new HashMap<>();
		for (ScheduleDtoWrapper schedule : schedules) {
			scheduleMap
				.computeIfAbsent(schedule.hallId(), k -> new ArrayList<>())
				.add(schedule);
		}

		for (Map.Entry<UUID, List<ScheduleDtoWrapper>> entry : scheduleMap.entrySet()) {
			List<ScheduleDtoWrapper> hallSchedules = entry.getValue();
			hallSchedules.sort(Comparator.comparing(ScheduleDtoWrapper::startAt));

			for (int i = 0; i < hallSchedules.size() - 1; i++) {
				ScheduleDtoWrapper current = hallSchedules.get(i);
				ScheduleDtoWrapper next = hallSchedules.get(i + 1);

				if (!current.endAt().isBefore(next.startAt())) {
					throw new PerformanceException(ResponseCode.PERFORMANCE_VALIDATION_EXCEPTION);
				}
			}
		}
	}

	private record ScheduleDtoWrapper(UUID hallId, LocalDateTime startAt, LocalDateTime endAt) {
	}

	public static void validatePerformanceData(UpdatePerformanceCommand command) {

		if (command.startAt() != null && command.endAt() != null) {
			if (!command.startAt().isBefore(command.endAt())) {
				throw new PerformanceException(ResponseCode.PERFORMANCE_VALIDATION_EXCEPTION, "공연 시작일은 종료일보다 빨라야 합니다.");
			}
		}
	}

	public static void validateScheduleDataForUpdate(UpdatePerformanceScheduleCommand schedule) {

		if (schedule.performanceHallId() == null) {
			throw new PerformanceException(ResponseCode.PERFORMANCE_VALIDATION_EXCEPTION, "공연장 ID는 필수입니다.");
		}

		if (schedule.startAt() != null && schedule.endAt() != null) {
			if (!schedule.startAt().isBefore(schedule.endAt())) {
				throw new PerformanceException(ResponseCode.PERFORMANCE_VALIDATION_EXCEPTION, "공연 시작일은 종료일보다 빨라야 합니다.");
			}
		}
	}
}
