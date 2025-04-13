package com.taken_seat.performance_service.performancehall.application.service;

import static com.taken_seat.performance_service.performancehall.application.dto.mapper.HallResponseMapper.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;
import com.taken_seat.performance_service.performancehall.application.dto.mapper.HallResponseMapper;
import com.taken_seat.performance_service.performancehall.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.application.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.SeatDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.SeatLayoutResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.UpdateResponseDto;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;
import com.taken_seat.performance_service.performancehall.domain.validation.PerformanceHallValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceHallService {

	public final PerformanceHallRepository performanceHallRepository;

	public final HallResponseMapper hallResponseMapper;

	public final PerformanceRepository performanceRepository;

	@Transactional
	public CreateResponseDto create(CreateRequestDto request, AuthenticatedUser authenticatedUser) {

		PerformanceHallValidator.validateAuthorized(authenticatedUser);

		PerformanceHallValidator.createValidateDuplicateHall(request.getName(), request.getAddress(),
			performanceHallRepository);

		PerformanceHallValidator.validateDuplicateSeats(request.getSeats());

		PerformanceHall performanceHall = PerformanceHall.create(request, authenticatedUser.getUserId());

		PerformanceHall saved = performanceHallRepository.save(performanceHall);

		return createHallToDto(saved);
	}

	@Transactional(readOnly = true)
	public PageResponseDto search(SearchFilterParam filterParam, Pageable pageable) {

		Page<PerformanceHall> pages = performanceHallRepository.findAll(filterParam, pageable);

		return hallResponseMapper.toPage(pages);
	}

	@Transactional(readOnly = true)
	public DetailResponseDto getDetail(UUID id) {

		PerformanceHall performanceHall = performanceHallRepository.findById(id)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_HALL_NOT_FOUND_EXCEPTION));

		return toDetail(performanceHall);
	}

	@Transactional
	public UpdateResponseDto update(UUID id, UpdateRequestDto request, AuthenticatedUser authenticatedUser) {

		PerformanceHallValidator.validateAuthorized(authenticatedUser);

		PerformanceHall performanceHall = performanceHallRepository.findById(id)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_HALL_NOT_FOUND_EXCEPTION));

		PerformanceHallValidator.updateValidateDuplicateHall(
			id, request.getName(), request.getAddress(), performanceHallRepository);

		PerformanceHallValidator.validateDuplicateSeats(request.getSeats());

		performanceHall.update(request);

		performanceHallRepository.save(performanceHall);

		return toUpdate(performanceHall);
	}

	@Transactional
	public void delete(UUID id, AuthenticatedUser authenticatedUser) {

		PerformanceHallValidator.validateAuthorized(authenticatedUser);

		PerformanceHall performanceHall = performanceHallRepository.findById(id)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_HALL_NOT_FOUND_EXCEPTION,
				"이미 삭제되었거나 존재하지 않는 공연장입니다."));

		performanceHall.delete(authenticatedUser.getUserId());
		performanceHallRepository.save(performanceHall);
	}

	@Transactional
	public BookingSeatClientResponseDto updateSeatStatus(BookingSeatClientRequestDto request) {

		PerformanceHall performanceHall = performanceHallRepository.findBySeatId(request.getSeatId())
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_HALL_NOT_FOUND_EXCEPTION));

		Seat seat = performanceHall.getSeatById(request.getSeatId());

		if (seat.getStatus() == SeatStatus.SOLDOUT) {
			return new BookingSeatClientResponseDto(null, false, "이미 선점된 좌석입니다.");
		}

		seat.updateStatus(SeatStatus.SOLDOUT);

		Performance performance = performanceRepository.findById(request.getPerformanceId())
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_NOT_FOUND_EXCEPTION));

		Integer price = performance.findPriceByScheduleAndSeatType(request.getPerformanceScheduleId(),
			seat.getSeatType());

		return new BookingSeatClientResponseDto(price, true, "좌석 선점에 성공했습니다.");
	}

	@Transactional
	public BookingSeatClientResponseDto cancelSeatStatus(BookingSeatClientRequestDto request) {

		PerformanceHall performanceHall = performanceHallRepository.findBySeatId(request.getSeatId())
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_HALL_NOT_FOUND_EXCEPTION));

		Seat seat = performanceHall.getSeatById(request.getSeatId());

		if (seat.getStatus() == SeatStatus.DISABLED) {
			throw new PerformanceException(ResponseCode.SEAT_STATUS_CHANGE_NOT_ALLOWED);
		}

		seat.updateStatus(SeatStatus.AVAILABLE);

		return new BookingSeatClientResponseDto(null, false, "좌석 선점이 취소되었습니다.");
	}

	@Transactional
	public SeatLayoutResponseDto getSeatLayout(UUID performanceScheduleId) {

		Performance performance = performanceRepository.findByPerformanceScheduleId(performanceScheduleId)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_SCHEDULE_NOT_FOUND_EXCEPTION));

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		UUID performanceHallId = schedule.getPerformanceHallId();

		PerformanceHall performanceHall = performanceHallRepository.findById(performanceHallId)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_HALL_NOT_FOUND_EXCEPTION));

		List<SeatDto> seatLayout = HallResponseMapper.toSeatLayout(performanceHall.getSeats());

		return new SeatLayoutResponseDto(seatLayout);
	}
}