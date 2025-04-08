package com.taken_seat.performance_service.performance.application.service;

import static com.taken_seat.performance_service.performance.application.dto.mapper.ResponseMapper.*;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Transactional
	public CreateResponseDto create(CreateRequestDto request) {

		Performance performance = Performance.create(request);

		Performance saved = performanceRepository.save(performance);

		return createToDto(saved);

	}

	@Transactional
	public void delete(UUID id, UUID deletedBy) {

		if (id == null) {
			throw new IllegalArgumentException("삭제할 수 없는 아이디입니다");
		}

		performanceRepository.deleteById(id, deletedBy);
	}
}
