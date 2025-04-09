package com.taken_seat.performance_service.performance.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.performance_service.performance.application.dto.request.CreatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.request.CreateSeatPriceDto;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.infrastructure.repository.PerformanceJpaRepository;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PerformanceServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PerformanceJpaRepository performanceJpaRepository;

	@PersistenceContext
	private EntityManager em;
	@Autowired
	private PerformanceService performanceService;

	public static CreateRequestDto createValidRequestDto() {
		return CreateRequestDto.builder()
			.title("뮤지컬 레미제라블")
			.description("프랑스 혁명을 배경으로 한 감동적인 뮤지컬")
			.startAt(LocalDateTime.of(2025, 6, 1, 19, 0))
			.endAt(LocalDateTime.of(2025, 6, 30, 21, 0))
			.posterUrl("https://image.example.com/posters/les-miserables.jpg")
			.ageLimit("15")
			.maxTicketCount(4)
			.discountInfo("조기 예매 시 10% 할인")
			.schedules(List.of(
				CreatePerformanceScheduleDto.builder()
					.performanceHallId(UUID.fromString("11111111-2222-3333-4444-555555555555"))
					.startAt(LocalDateTime.of(2025, 6, 10, 19, 0))
					.endAt(LocalDateTime.of(2025, 6, 10, 21, 0))
					.saleStartAt(LocalDateTime.of(2025, 5, 1, 10, 0))
					.saleEndAt(LocalDateTime.of(2025, 6, 9, 23, 59, 59))
					.seatPrices(List.of(
						CreateSeatPriceDto.builder()
							.seatType(SeatType.VIP)
							.price(150000)
							.build(),
						CreateSeatPriceDto.builder()
							.seatType(SeatType.R)
							.price(120000)
							.build()
					))
					.build()
			))
			.build();
	}

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

	/**
	 * 공연 삭제 성공 시
	 */
	@Test
	@DisplayName("공연 삭제 성공 시 deletedAt, deletedBy가 정상적으로 반영되어야 한다")
	void deletePerformance_shouldSoftDelete() throws Exception {
		// given
		CreateRequestDto requestDto = createValidRequestDto();
		Performance performance = Performance.create(requestDto);
		performanceJpaRepository.save(performance);
		em.flush();
		em.clear();

		UUID userId = UUID.randomUUID();

		// when
		mockMvc.perform(delete("/api/v1/performances/" + performance.getId())
				.param("deletedBy", userId.toString()))
			.andExpect(status().isNoContent());

		// then
		Performance deletedPerformance = em.find(Performance.class, performance.getId());

		assertThat(deletedPerformance.getDeletedAt()).isNotNull();
		assertThat(deletedPerformance.getDeletedBy()).isEqualTo(userId);
	}

	/**
	 * 공연 실패 케이스 삭제하려는 ID가 없는 경우
	 */
	@Test
	@DisplayName("삭제 ID가 null이면 예외가 발생한다")
	void deletePerformance_shouldFail_whenIdIsNull() {

		// given
		UUID deletedBy = UUID.randomUUID();

		// when & then
		assertThatThrownBy(() -> performanceService.delete(null, deletedBy))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("삭제할 수 없는 아이디입니다");
	}
}