package com.taken_seat.performance_service.performance.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.response.PerformanceEndTimeDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceExistenceValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceClientService {

	private final PerformanceExistenceValidator performanceExistenceValidator;

	@Transactional
	public PerformanceEndTimeDto getPerformanceEndTime(UUID performanceId, UUID performanceScheduleId) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(performanceId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		return new PerformanceEndTimeDto(schedule.getEndAt());
	}

	@Transactional
	public PerformanceStartTimeDto getPerformanceStartTime(UUID performanceId, UUID performanceScheduleId) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(performanceId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		return new PerformanceStartTimeDto(schedule.getStartAt());
	}
}
