package com.taken_seat.performance_service.performance.domain.validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.application.dto.request.CreatePerformanceScheduleDto;

public class PerformanceValidator {

	public static void validateDuplicateSchedules(List<CreatePerformanceScheduleDto> schedules) {
		// 1. 공연장 ID별로 스케줄 그룹화
		Map<UUID, List<CreatePerformanceScheduleDto>> scheduleMap = new HashMap<>();
		for (CreatePerformanceScheduleDto schedule : schedules) {
			scheduleMap
				.computeIfAbsent(schedule.getPerformanceHallId(), k -> new ArrayList<>())
				.add(schedule);
		}

		// 2. 공연장 별로 처리
		for (Map.Entry<UUID, List<CreatePerformanceScheduleDto>> entry : scheduleMap.entrySet()) {
			List<CreatePerformanceScheduleDto> hallSchedules = entry.getValue();

			// 3. 시작 시간 기준 정렬
			hallSchedules.sort(Comparator.comparing(CreatePerformanceScheduleDto::getStartAt));

			// 4. 정렬된 리스트 순회하면서 인접 스케줄 비교 (O(N log N))
			for (int i = 0; i < hallSchedules.size() - 1; i++) {
				CreatePerformanceScheduleDto current = hallSchedules.get(i);
				CreatePerformanceScheduleDto next = hallSchedules.get(i + 1);

				// 현재 스케줄의 종료 시간이 다음 스케줄의 시작 시간보다 이후면 겹침
				if (!current.getEndAt().isBefore(next.getStartAt())) {
					throw new PerformanceException(ResponseCode.PERFORMANCE_VALIDATION_EXCEPTION);
				}
			}
		}
	}
}
