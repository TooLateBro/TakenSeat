package com.taken_seat.performance_service.performancehall.application.service;

import static com.taken_seat.performance_service.performancehall.application.dto.mapper.HallResponseMapper.*;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.performance_service.performancehall.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceHallService {

	public final PerformanceHallRepository performanceHallRepository;

	public CreateResponseDto create(CreateRequestDto request) {

		boolean exists = performanceHallRepository.existsByNameAndAddress(
			request.getName(), request.getAddress());

		if (exists) {
			throw new IllegalArgumentException("이미 존재하는 공연장입니다.");
		}

		PerformanceHall performanceHall = PerformanceHall.create(request);

		PerformanceHall saved = performanceHallRepository.save(performanceHall);

		return createHallToDto(saved);
	}

	public DetailResponseDto getDetail(UUID id) {

		PerformanceHall performanceHall = performanceHallRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("공연장 정보를 찾을 수 없습니다"));

		return toDetail(performanceHall);
	}
}
