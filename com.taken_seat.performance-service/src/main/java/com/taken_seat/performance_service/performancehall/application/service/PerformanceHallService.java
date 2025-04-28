package com.taken_seat.performance_service.performancehall.application.service;

import static com.taken_seat.performance_service.common.config.RedisCacheConfig.*;
import static com.taken_seat.performance_service.performancehall.application.dto.mapper.HallResponseMapper.*;

import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performancehall.application.dto.command.CreatePerformanceHallCommand;
import com.taken_seat.performance_service.performancehall.application.dto.command.UpdatePerformanceHallCommand;
import com.taken_seat.performance_service.performancehall.application.dto.mapper.HallCreateCommandMapper;
import com.taken_seat.performance_service.performancehall.application.dto.mapper.HallResponseMapper;
import com.taken_seat.performance_service.performancehall.application.dto.mapper.HallUpdateCommandMapper;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;
import com.taken_seat.performance_service.performancehall.domain.validation.PerformanceHallExistenceValidator;
import com.taken_seat.performance_service.performancehall.domain.validation.PerformanceHallValidator;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallCreateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallSearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallUpdateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallCreateResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallDetailResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallPageResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallSearchResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallUpdateResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceHallService {

	private final PerformanceHallRepository performanceHallRepository;
	private final HallResponseMapper hallResponseMapper;
	private final PerformanceHallExistenceValidator performanceHallExistenceValidator;
	private final HallCreateCommandMapper hallCreateCommandMapper;
	private final HallUpdateCommandMapper hallUpdateCommandMapper;

	@Transactional
	public HallCreateResponseDto create(HallCreateRequestDto request, AuthenticatedUser authenticatedUser) {

		PerformanceHallValidator.validateAuthorized(authenticatedUser);

		CreatePerformanceHallCommand command = hallCreateCommandMapper.toCommand(request);

		PerformanceHallValidator.createValidateDuplicateHall(
			command.name(), command.address(),
			performanceHallRepository);

		PerformanceHallValidator.validateDuplicateSeats(command.seats());

		PerformanceHall performanceHall =
			PerformanceHall.create(command, authenticatedUser.getUserId());

		PerformanceHall saved = performanceHallRepository.save(performanceHall);

		return createHallToDto(saved);
	}

	@Cacheable(
		cacheNames = PERFORMANCE_HALL_SEARCH,
		key = "#filterParam.toString() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize",
		unless = "#result == null"
	)
	@Transactional(readOnly = true)
	public HallPageResponseDto search(HallSearchFilterParam filterParam, Pageable pageable) {

		Page<HallSearchResponseDto> pages =
			performanceHallRepository.findAll(filterParam, pageable);

		return hallResponseMapper.toPage(pages);
	}

	@Cacheable(
		cacheNames = PERFORMANCE_HALL_DETAIL,
		key = "#id",
		unless = "#result == null"
	)
	@Transactional(readOnly = true)
	public HallDetailResponseDto getDetail(UUID id) {

		PerformanceHall performanceHall =
			performanceHallExistenceValidator.validateByPerformanceHallId(id);

		return toDetail(performanceHall);
	}

	@Transactional
	public HallUpdateResponseDto update(UUID id, HallUpdateRequestDto request, AuthenticatedUser authenticatedUser) {

		PerformanceHallValidator.validateAuthorized(authenticatedUser);

		PerformanceHall performanceHall =
			performanceHallExistenceValidator.validateByPerformanceHallId(id);

		UpdatePerformanceHallCommand command = hallUpdateCommandMapper.toCommand(request);

		PerformanceHallValidator.updateValidateDuplicateHall(
			id, command.name(), command.address(), performanceHallRepository);

		PerformanceHallValidator.validateDuplicateSeats(command.seats());

		performanceHall.update(command);

		performanceHallRepository.save(performanceHall);

		return toUpdate(performanceHall);
	}

	@Transactional
	public void delete(UUID id, AuthenticatedUser authenticatedUser) {

		PerformanceHallValidator.validateAuthorized(authenticatedUser);

		PerformanceHall performanceHall =
			performanceHallExistenceValidator.validateByPerformanceHallId(id);

		performanceHall.delete(authenticatedUser.getUserId());
	}
}