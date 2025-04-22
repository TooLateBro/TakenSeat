package com.taken_seat.performance_service.performance.domain.validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.application.dto.request.CreatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.UpdatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.UpdateRequestDto;

public class PerformanceValidator {

	public static void validateAuthorized(AuthenticatedUser authenticatedUser) {
		String role = authenticatedUser.getRole();
		if (!(role.equals("ADMIN") || role.equals("MANAGER") || role.equals("PRODUCER"))) {
			throw new PerformanceException(ResponseCode.ACCESS_DENIED_EXCEPTION, "접근 권한이 없습니다.");
		}
	}

	public static void validateDuplicateSchedules(List<CreatePerformanceScheduleDto> schedules) {

		validateDuplicateScheduleLogic(schedules.stream()
			.map(dto -> new ScheduleDtoWrapper(dto.getPerformanceHallId(), dto.getStartAt(), dto.getEndAt()))
			.toList());
	}

	public static void validateDuplicateSchedulesForUpdate(List<UpdatePerformanceScheduleDto> schedules) {

		validateDuplicateScheduleLogic(schedules.stream()
			.map(dto -> new ScheduleDtoWrapper(dto.getPerformanceHallId(), dto.getStartAt(), dto.getEndAt()))
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

	public static void validatePerformanceData(UpdateRequestDto request) {

		if (request.getStartAt() != null && request.getEndAt() != null) {
			if (!request.getStartAt().isBefore(request.getEndAt())) {
				throw new PerformanceException(ResponseCode.PERFORMANCE_VALIDATION_EXCEPTION, "공연 시작일은 종료일보다 빨라야 합니다.");
			}
		}
	}

	public static void validateScheduleDataForUpdate(UpdatePerformanceScheduleDto schedule) {

		if (schedule.getPerformanceHallId() == null) {
			throw new PerformanceException(ResponseCode.PERFORMANCE_VALIDATION_EXCEPTION, "공연장 ID는 필수입니다.");
		}

		if (schedule.getStartAt() != null && schedule.getEndAt() != null) {
			if (!schedule.getStartAt().isBefore(schedule.getEndAt())) {
				throw new PerformanceException(ResponseCode.PERFORMANCE_VALIDATION_EXCEPTION, "공연 시작일은 종료일보다 빨라야 합니다.");
			}
		}
	}
}
