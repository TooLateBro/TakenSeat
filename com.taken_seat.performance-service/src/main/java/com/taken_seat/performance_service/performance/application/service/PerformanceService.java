package com.taken_seat.performance_service.performance.application.service;

import static com.taken_seat.performance_service.performance.application.dto.mapper.ResponseMapper.*;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
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
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceService {

	private final PerformanceRepository performanceRepository;
	private final ResponseMapper responseMapper;

	@Transactional
	public CreateResponseDto create(CreateRequestDto request, AuthenticatedUser authenticatedUser) {

		if (!isAuthorized(authenticatedUser)) {
			throw new PerformanceException(ResponseCode.ACCESS_DENIED_EXCEPTION, "접근 권한이 없습니다.");
		}

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

		Performance performance = performanceRepository.findById(id)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_NOT_FOUND_EXCEPTION));

		return detailToDto(performance);
	}

	@Transactional
	public UpdateResponseDto update(UUID id, UpdateRequestDto request, AuthenticatedUser authenticatedUser) {

		if (!isAuthorized(authenticatedUser)) {
			throw new PerformanceException(ResponseCode.ACCESS_DENIED_EXCEPTION, "접근 권한이 없습니다.");
		}

		for (UpdatePerformanceScheduleDto schedule : request.getSchedules()) {
			PerformanceValidator.validateScheduleDataForUpdate(schedule);
		}

		PerformanceValidator.validateDuplicateSchedulesForUpdate(request.getSchedules());

		Performance performance = performanceRepository.findById(id)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_NOT_FOUND_EXCEPTION));

		performance.update(request, authenticatedUser.getUserId());

		return toUpdate(performance);
	}

	@Transactional
	public void delete(UUID id, UUID deletedBy) {

		if (id == null) {
			throw new IllegalArgumentException("삭제할 공연 ID는 필수입니다");
		}

		performanceRepository.deleteById(id, deletedBy);
	}

	@Transactional
	public PerformanceEndTimeDto getPerformanceEndTime(UUID performanceId, UUID performanceScheduleId) {
		Performance performance = performanceRepository.findById(performanceId)
			.orElseThrow(() -> new IllegalArgumentException("해당 공연이 존재하지 않습니다."));

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		return new PerformanceEndTimeDto(schedule.getEndAt());
	}

	private boolean isAuthorized(AuthenticatedUser authenticatedUser) {

		String role = authenticatedUser.getRole();
		return role.equals("ADMIN") || role.equals("MANAGER") || role.equals("PRODUCER");
	}
}
