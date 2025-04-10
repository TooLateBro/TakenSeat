package com.taken_seat.performance_service.performenceticket.application.service;

import org.springframework.stereotype.Service;

import com.taken_seat.common_service.dto.request.TicketPerformanceClientRequest;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformenceTicketService {

	public final PerformanceRepository performanceRepository;
	public final PerformanceHallRepository performanceHallRepository;

	public TicketPerformanceClientResponse getPerformanceInfo(TicketPerformanceClientRequest request) {

		Performance performance = performanceRepository.findByPerformanceScheduleId(request.getPerformanceScheduleId())
			.orElseThrow(() -> new IllegalArgumentException("공연 회차가 존재하지 않습니다"));

		PerformanceSchedule schedule = performance.getScheduleById(request.getPerformanceScheduleId());

		PerformanceHall performanceHall = performanceHallRepository.findById((schedule.getPerformanceHallId()))
			.orElseThrow(() -> new IllegalArgumentException("공연장 정보가 존재하지 않습니다"));

		Seat seat = performanceHall.getSeatById(request.getSeatId());

		return TicketPerformanceClientResponse.builder()
			.title(performance.getTitle())
			.startAt(schedule.getStartAt())
			.endAt(schedule.getEndAt())
			.name(performanceHall.getName())
			.address(performanceHall.getAddress())
			.seatNumber(seat.getSeatNumber())
			.seatRowNumber(seat.getRowNumber())
			.seatType(seat.getSeatType().name())
			.build();
	}
}
