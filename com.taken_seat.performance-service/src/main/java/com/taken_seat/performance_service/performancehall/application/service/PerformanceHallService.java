package com.taken_seat.performance_service.performancehall.application.service;

import static com.taken_seat.performance_service.performancehall.application.dto.mapper.HallResponseMapper.*;

import java.util.UUID;

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
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallQueryRepository;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;
import com.taken_seat.performance_service.performancehall.domain.validation.PerformanceHallExistenceValidator;
import com.taken_seat.performance_service.performancehall.domain.validation.PerformanceHallValidator;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.SearchResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.UpdateResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceHallService {

	private final PerformanceHallRepository performanceHallRepository;
	private final HallResponseMapper hallResponseMapper;
	private final PerformanceHallExistenceValidator performanceHallExistenceValidator;
	private final PerformanceHallQueryRepository performanceHallQueryRepository;
	private final HallCreateCommandMapper hallCreateCommandMapper;
	private final HallUpdateCommandMapper hallUpdateCommandMapper;

	@Transactional
	public CreateResponseDto create(CreateRequestDto request, AuthenticatedUser authenticatedUser) {

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

	@Transactional(readOnly = true)
	public PageResponseDto search(SearchFilterParam filterParam, Pageable pageable) {

		Page<SearchResponseDto> pages =
			performanceHallQueryRepository.searchByFilter(filterParam, pageable);

		return hallResponseMapper.toPage(pages);
	}

	@Transactional(readOnly = true)
	public DetailResponseDto getDetail(UUID id) {

		PerformanceHall performanceHall =
			performanceHallExistenceValidator.validateByPerformanceHallId(id);

		return toDetail(performanceHall);
	}

	@Transactional
	public UpdateResponseDto update(UUID id, UpdateRequestDto request, AuthenticatedUser authenticatedUser) {

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