package com.taken_seat.performance_service.performance.domain.helper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.taken_seat.performance_service.performance.application.dto.request.UpdatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.UpdateSeatPriceDto;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSeatPrice;

public class PerformanceUpdateHelper {

	// 공연 전체 회차 목록 업데이트
	public static void updateSchedules(Performance performance, List<UpdatePerformanceScheduleDto> scheduleDtos,
		UUID updatedBy) {

		for (UpdatePerformanceScheduleDto dto : scheduleDtos) {
			Optional<PerformanceSchedule> matched = performance.getSchedules().stream()
				.filter(s -> s.getId().equals(dto.getPerformanceScheduleId()))
				.findFirst();

			if (matched.isPresent()) {
				updateSchedule(matched.get(), dto, updatedBy);
			} else {
				PerformanceSchedule newSchedule = createNewSchedule(dto, performance, updatedBy);
				performance.getSchedules().add(newSchedule);
			}
		}
	}

	// 회차 정보 업데이트
	private static void updateSchedule(PerformanceSchedule schedule, UpdatePerformanceScheduleDto request,
		UUID updatedBy) {

		schedule.preUpdate(updatedBy);
		schedule.update(request);

		updateSeatPrices(schedule, request.getSeatPrices(), updatedBy);
	}

	// 새로운 회차 생성
	private static PerformanceSchedule createNewSchedule(UpdatePerformanceScheduleDto request, Performance performance,
		UUID updatedBy) {

		PerformanceSchedule schedule = PerformanceSchedule.builder()
			.performance(performance)
			.performanceHallId(request.getPerformanceHallId())
			.startAt(request.getStartAt())
			.endAt(request.getEndAt())
			.saleStartAt(request.getSaleStartAt())
			.saleEndAt(request.getSaleEndAt())
			.status(request.getStatus())
			.build();

		schedule.prePersist(updatedBy);
		updateSeatPrices(schedule, request.getSeatPrices(), updatedBy);
		return schedule;
	}

	// 회차에 포함된 좌석 가격 리스트 업데이트
	private static void updateSeatPrices(PerformanceSchedule schedule, List<UpdateSeatPriceDto> seatPrices,
		UUID updatedBy) {

		if (seatPrices == null)
			return;

		for (UpdateSeatPriceDto seatPrice : seatPrices) {
			Optional<PerformanceSeatPrice> matched = schedule.getSeatPrices().stream()
				.filter(p -> p.getId().equals(seatPrice.getPerformanceSeatPriceId()))
				.findFirst();

			if (matched.isPresent()) {
				matched.get().update(seatPrice);
			} else {
				PerformanceSeatPrice newPrice = PerformanceSeatPrice.builder()
					.performanceSchedule(schedule)
					.seatType(seatPrice.getSeatType())
					.price(seatPrice.getPrice())
					.build();
				newPrice.prePersist(updatedBy);
				schedule.getSeatPrices().add(newPrice);
			}
		}
	}
}

