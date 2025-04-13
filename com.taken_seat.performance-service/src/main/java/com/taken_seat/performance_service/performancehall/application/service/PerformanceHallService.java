package com.taken_seat.performance_service.performancehall.application.service;

import static com.taken_seat.performance_service.performancehall.application.dto.mapper.HallResponseMapper.*;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performancehall.application.dto.mapper.HallResponseMapper;
import com.taken_seat.performance_service.performancehall.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.application.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.UpdateResponseDto;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceHallService {

	public final PerformanceHallRepository performanceHallRepository;

	public final HallResponseMapper hallResponseMapper;

	public CreateResponseDto create(CreateRequestDto request, AuthenticatedUser authenticatedUser) {

		if (!isAuthorized(authenticatedUser)) {
			throw new PerformanceException(ResponseCode.ACCESS_DENIED_EXCEPTION, "접근 권한이 없습니다.");
		}

		boolean exists = performanceHallRepository.existsByNameAndAddress(
			request.getName(), request.getAddress());

		if (exists) {
			throw new PerformanceException(ResponseCode.PERFORMANCE_HALL_ALREADY_EXISTS);
		}

		PerformanceHall performanceHall = PerformanceHall.create(request, authenticatedUser.getUserId());

		PerformanceHall saved = performanceHallRepository.save(performanceHall);

		return createHallToDto(saved);
	}

	@Transactional(readOnly = true)
	public PageResponseDto search(SearchFilterParam filterParam, Pageable pageable) {

		Page<PerformanceHall> pages = performanceHallRepository.findAll(filterParam, pageable);

		return hallResponseMapper.toPage(pages);
	}

	public DetailResponseDto getDetail(UUID id) {

		PerformanceHall performanceHall = performanceHallRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("공연장 정보를 찾을 수 없습니다"));

		return toDetail(performanceHall);
	}

	public UpdateResponseDto update(UUID id, UpdateRequestDto request) {

		PerformanceHall performanceHall = performanceHallRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 공연장을 찾을 수 없습니다"));

		performanceHall.update(request);

		performanceHallRepository.save(performanceHall);

		return toUpdate(performanceHall);
	}

	public void delete(UUID id, UUID deletedBy) {

		if (id == null) {
			throw new IllegalArgumentException("삭제할 공연 ID는 필수입니다");
		}

		performanceHallRepository.deleteById(id, deletedBy);
	}

	private boolean isAuthorized(AuthenticatedUser authenticatedUser) {

		String role = authenticatedUser.getRole();
		return role.equals("ADMIN") || role.equals("MANAGER");
	}
}