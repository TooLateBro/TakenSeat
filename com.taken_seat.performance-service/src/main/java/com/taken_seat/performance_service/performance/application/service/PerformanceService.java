package com.taken_seat.performance_service.performance.application.service;

import static com.taken_seat.performance_service.common.config.RedisCacheConfig.*;
import static com.taken_seat.performance_service.performance.application.dto.mapper.PerformanceResponseMapper.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.aop.TrackLatency;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performance.application.dto.command.CreatePerformanceCommand;
import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceCommand;
import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceScheduleCommand;
import com.taken_seat.performance_service.performance.application.dto.mapper.PerformanceCreateCommandMapper;
import com.taken_seat.performance_service.performance.application.dto.mapper.PerformanceResponseMapper;
import com.taken_seat.performance_service.performance.application.dto.mapper.PerformanceUpdateCommandMapper;
import com.taken_seat.performance_service.performance.domain.helper.PerformanceCreateHelper;
import com.taken_seat.performance_service.performance.domain.helper.PerformanceUpdateHelper;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;
import com.taken_seat.performance_service.performance.domain.repository.redis.PerformanceRankingRedisRepository;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceExistenceValidator;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceValidator;
import com.taken_seat.performance_service.performance.presentation.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.presentation.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.PerformanceRankingResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.SearchResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.UpdateResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceService {

	private final PerformanceRepository performanceRepository;
	private final PerformanceResponseMapper performanceResponseMapper;
	private final PerformanceExistenceValidator performanceExistenceValidator;
	private final PerformanceCreateCommandMapper performanceCreateCommandMapper;
	private final PerformanceUpdateCommandMapper performanceUpdateCommandMapper;
	private final PerformanceCreateHelper performanceCreateHelper;
	private final PerformanceRankingRedisRepository performanceRankingRedisRepository;
	private final NotNullValidator notNullValidator;

	@Transactional
	public CreateResponseDto create(CreateRequestDto request, AuthenticatedUser authenticatedUser) {

		CreatePerformanceCommand command = performanceCreateCommandMapper.toCommand(request);

		PerformanceValidator.validateDuplicateSchedules(command.schedules());

		Performance performance = performanceCreateHelper.createPerformance(command, authenticatedUser.getUserId());

		List<PerformanceSchedule> schedules = performanceCreateHelper.createPerformanceSchedules(
			command.schedules(), performance, authenticatedUser.getUserId()
		);

		performance.addSchedules(schedules);

		Performance saved = performanceRepository.save(performance);

		return createToDto(saved);
	}

	@TrackLatency(
		value = "performance_search_seconds",
		description = "공연 목록 조회 API 처리 시간(초)"
	)
	@Cacheable(
		cacheNames = PERFORMANCE_SEARCH,
		key = "#filterParam.toString() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize",
		unless = "#result == null"
	)
	@Transactional(readOnly = true)
	public PageResponseDto search(SearchFilterParam filterParam, Pageable pageable) {

		Page<SearchResponseDto> pages = performanceRepository.findAll(filterParam, pageable);

		return performanceResponseMapper.toPage(pages);
	}

	@TrackLatency(
		value = "performance_detail_seconds",
		description = "공연 상세 조회 API 처리 시간(초)"
	)
	@Cacheable(
		cacheNames = PERFORMANCE_DETAIL,
		key = "#id",
		unless = "#result == null"
	)
	@Transactional(readOnly = true)
	public DetailResponseDto getDetail(UUID id) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(id);

		performanceRankingRedisRepository.incrementScore(id, 1.0);

		return detailToDto(performance);
	}

	@Transactional
	public UpdateResponseDto update(UUID id, UpdateRequestDto request, AuthenticatedUser authenticatedUser) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(id);

		UpdatePerformanceCommand command = performanceUpdateCommandMapper.toCommand(request);

		PerformanceValidator.validatePerformanceData(command);

		for (UpdatePerformanceScheduleCommand schedule : command.schedules()) {
			PerformanceValidator.validateScheduleDataForUpdate(schedule);
		}

		PerformanceValidator.validateDuplicateSchedulesForUpdate(command.schedules());

		performance.update(command, authenticatedUser.getUserId());

		return toUpdate(performance);
	}

	@Transactional
	public void delete(UUID id, AuthenticatedUser authenticatedUser) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(id);

		performance.delete(authenticatedUser.getUserId());

		performanceRepository.save(performance);
	}

	@Transactional
	public void updateStatus(UUID id, AuthenticatedUser authenticatedUser) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(id);

		PerformanceUpdateHelper.updateStatus(performance, authenticatedUser);

		performanceRepository.save(performance);
	}

	@Transactional
	public void deletePerformanceSchedule(
		UUID performanceId,
		UUID performanceScheduleId,
		AuthenticatedUser authenticatedUser) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(performanceId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		schedule.delete(authenticatedUser.getUserId());

		performanceRepository.save(performance);
	}

	@Transactional
	public List<PerformanceRankingResponseDto> getTopRankedPerformances(int limit) {

		List<UUID> topPerformanceIds
			= performanceRankingRedisRepository.getTopPerformanceIds(limit);

		if (topPerformanceIds.isEmpty()) {
			return List.of();
		}

		List<Performance> performances =
			performanceRepository.findAllById(topPerformanceIds);

		Map<UUID, Performance> performanceMap =
			performances.stream()
				.collect(Collectors.toMap(Performance::getId, performance -> performance));

		return topPerformanceIds.stream()
			.map(performanceMap::get)
			.filter(Objects::nonNull)
			.map(PerformanceResponseMapper::toRankingResponse)
			.toList();
	}
}
