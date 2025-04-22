package com.taken_seat.performance_service.performance.application.service;

import static com.taken_seat.performance_service.performance.application.dto.mapper.ResponseMapper.*;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.performance_service.performance.application.dto.mapper.ResponseMapper;
import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.application.dto.request.UpdatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.PerformanceEndTimeDto;
import com.taken_seat.performance_service.performance.application.dto.response.UpdateResponseDto;
import com.taken_seat.performance_service.performance.domain.helper.PerformanceUpdateHelper;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceExistenceValidator;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceValidator;
import com.taken_seat.performance_service.performancehall.domain.facade.PerformanceHallFacade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceService {

	private final PerformanceRepository performanceRepository;
	private final ResponseMapper responseMapper;
	private final PerformanceHallFacade performanceHallFacade;
	private final PerformanceExistenceValidator performanceExistenceValidator;

	@Transactional
	public CreateResponseDto create(CreateRequestDto request, AuthenticatedUser authenticatedUser) {

		PerformanceValidator.validateAuthorized(authenticatedUser);

		PerformanceValidator.validateDuplicateSchedules(request.getSchedules());

		Performance performance = Performance.create(request, authenticatedUser.getUserId());

		Performance saved = performanceRepository.save(performance);

		return createToDto(saved);
	}

	@Transactional(readOnly = true)
	public PageResponseDto search(SearchFilterParam filterParam, Pageable pageable) {

		Page<Performance> pages = performanceRepository.findAll(filterParam, pageable);

		return responseMapper.toPage(pages);
	}

	@Transactional(readOnly = true)
	public DetailResponseDto getDetail(UUID id) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(id);

		return detailToDto(performance);
	}

	@Transactional
	public UpdateResponseDto update(UUID id, UpdateRequestDto request, AuthenticatedUser authenticatedUser) {

		PerformanceValidator.validateAuthorized(authenticatedUser);

		PerformanceValidator.validatePerformanceData(request);

		for (UpdatePerformanceScheduleDto schedule : request.getSchedules()) {
			PerformanceValidator.validateScheduleDataForUpdate(schedule);
		}

		PerformanceValidator.validateDuplicateSchedulesForUpdate(request.getSchedules());

		Performance performance = performanceExistenceValidator.validateByPerformanceId(id);

		performance.update(request, authenticatedUser.getUserId());

		return toUpdate(performance);
	}

	@Transactional
	public void delete(UUID id, AuthenticatedUser authenticatedUser) {

		PerformanceValidator.validateAuthorized(authenticatedUser);

		Performance performance = performanceExistenceValidator.validateByPerformanceId(id);

		performance.delete(authenticatedUser.getUserId());

		performanceRepository.save(performance);
	}

	@Transactional
	public PerformanceEndTimeDto getPerformanceEndTime(UUID performanceId, UUID performanceScheduleId) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(performanceId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		return new PerformanceEndTimeDto(schedule.getEndAt());
	}

	@Transactional
	public void updateStatus(UUID id, AuthenticatedUser authenticatedUser) {

		PerformanceValidator.validateAuthorized(authenticatedUser);

		Performance performance = performanceExistenceValidator.validateByPerformanceId(id);

		PerformanceUpdateHelper.updateStatus(performance, authenticatedUser, performanceHallFacade);

		performanceRepository.save(performance);
	}

	@Transactional
	public PerformanceStartTimeDto getPerformanceStartTime(UUID performanceId, UUID performanceScheduleId) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(performanceId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		return new PerformanceStartTimeDto(schedule.getStartAt());
	}
}
