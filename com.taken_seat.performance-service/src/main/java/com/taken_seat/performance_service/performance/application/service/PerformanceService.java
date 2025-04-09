package com.taken_seat.performance_service.performance.application.service;

import static com.taken_seat.performance_service.performance.application.dto.mapper.ResponseMapper.*;

import org.springframework.stereotype.Service;

import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceService {

	private final PerformanceRepository performanceRepository;

	public CreateResponseDto create(CreateRequestDto request) {

		Performance performance = Performance.create(request);

		Performance saved = performanceRepository.save(performance);

		return createToDto(saved);

	}
}
