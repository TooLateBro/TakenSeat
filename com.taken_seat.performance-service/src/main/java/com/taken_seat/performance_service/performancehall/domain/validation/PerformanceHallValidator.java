package com.taken_seat.performance_service.performancehall.domain.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.BaseSeatDto;

public class PerformanceHallValidator {

	/**
	 * 공연장 생성 시, 공연장 이름 & 주소 중복 확인
	 */
	public static void createValidateDuplicateHall(String name, String address,
		PerformanceHallRepository performanceHallRepository) {
		if (performanceHallRepository.existsByNameAndAddress(name, address)) {
			throw new PerformanceException(ResponseCode.PERFORMANCE_HALL_ALREADY_EXISTS);
		}
	}

	/**
	 * 공연장 수정 시, 현재 공연 Id 제외하고 공연장 이름 & 주소 중복 확인
	 */
	public static void updateValidateDuplicateHall(UUID id, String name, String address,
		PerformanceHallRepository performanceHallRepository) {
		if (performanceHallRepository.existsByNameAndAddressAndIdNot(name, address, id)) {
			throw new PerformanceException(ResponseCode.PERFORMANCE_HALL_ALREADY_EXISTS);
		}
	}

	/**
	 * 공연 좌석 등록 및 수정 시, 같은 위치(rowNumber + seatNumber)에 중복 좌석이 있는지 확인
	 */
	public static void validateDuplicateSeats(List<? extends BaseSeatDto> seats) {

		if (seats == null) {
			return;
		}

		Set<String> seatKeys = new HashSet<>();

		for (BaseSeatDto seat : seats) {
			String key = seat.getRowNumber() + seat.getSeatNumber();

			if (!seatKeys.add(key)) {
				throw new PerformanceException(ResponseCode.PERFORMANCE_HALL_DUPLICATE_SEAT,
					"중복된 좌석(rowNumber + seatNumber)이 존재합니다.");
			}
		}
	}
}
