package com.taken_seat.performance_service.performance.application.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.taken_seat.performance_service.performance.application.dto.request.CreatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.request.CreateSeatPriceDto;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

class PerformanceServiceTest {

	/**
	 * 메서드 호출 시 스케줄과 좌석 가격이 정상적으로 생성이 되는지 단위 테스트 진행
	 */
	@DisplayName("Performance.create()는 요청을 받아 schedules와 seatPrices를 생성한다")
	@Test
	void create() {

		// given
		UUID performanceHallId = UUID.randomUUID();

		CreateSeatPriceDto seatPrice1 = new CreateSeatPriceDto(SeatType.VIP, 150000);
		CreateSeatPriceDto seatPrice2 = new CreateSeatPriceDto(SeatType.R, 120000);

		CreatePerformanceScheduleDto scheduleDto = CreatePerformanceScheduleDto.builder()
			.performanceHallId(performanceHallId)
			.startAt(LocalDateTime.of(2025, 6, 10, 19, 0))
			.endAt(LocalDateTime.of(2025, 6, 10, 21, 0))
			.saleStartAt(LocalDateTime.of(2025, 5, 1, 10, 0))
			.saleEndAt(LocalDateTime.of(2025, 6, 9, 23, 59))
			.seatPrices(List.of(seatPrice1, seatPrice2))
			.build();

		CreateRequestDto requestDto = CreateRequestDto.builder()
			.title("레미제라블")
			.description("감동적인 뮤지컬")
			.startAt(LocalDateTime.of(2025, 6, 1, 19, 0))
			.endAt(LocalDateTime.of(2025, 6, 30, 21, 0))
			.posterUrl("url")
			.ageLimit("15세 미만")
			.maxTicketCount(4)
			.discountInfo("조기 예매 시 10%")
			.schedules(List.of(scheduleDto))
			.build();

		// when
		Performance performance = Performance.create(requestDto);

		// then
		assertThat(performance.getSchedules()).hasSize(1);
		assertThat(performance.getSchedules().get(0).getSeatPrices()).hasSize(2);
		assertThat(performance.getSchedules().get(0).getSeatPrices().get(0).getSeatType()).isEqualTo(SeatType.VIP);
		assertThat(performance.getSchedules().get(0).getSeatPrices().get(1).getPrice()).isEqualTo(120000);
	}
}