package com.taken_seat.performance_service.performance.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taken_seat.performance_service.performance.application.dto.request.CreatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.request.CreateSeatPriceDto;
import com.taken_seat.performance_service.performance.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.PerformanceScheduleResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.SeatPriceResponseDto;
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

	@Test
	@DisplayName("공연 상세 조회 성공 시 응답 dto가 반환되는지 테스트")
	void getDetail() throws Exception {
		// given
		CreateRequestDto requestDto = createValidRequestDto();
		Performance performance = Performance.create(requestDto);
		performanceJpaRepository.save(performance);

		// when
		MvcResult result = mockMvc.perform(get("/api/v1/performances/" + performance.getId()))
			.andExpect(status().isOk())
			.andReturn();

		String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		// then: 응답을 DetailResponseDto로 변환하여 값 비교
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		DetailResponseDto detail = mapper.readValue(responseBody, DetailResponseDto.class);

		// 공연 정보 검증
		assertThat(detail.getPerformanceId()).isEqualTo(performance.getId());
		assertThat(detail.getTitle()).isEqualTo(performance.getTitle());
		assertThat(detail.getDescription()).isEqualTo(performance.getDescription());
		assertThat(detail.getStartAt()).isEqualTo(performance.getStartAt());
		assertThat(detail.getEndAt()).isEqualTo(performance.getEndAt());
		assertThat(detail.getStatus()).isEqualTo(performance.getStatus());
		assertThat(detail.getPosterUrl()).isEqualTo(performance.getPosterUrl());
		assertThat(detail.getAgeLimit()).isEqualTo(performance.getAgeLimit());
		assertThat(detail.getMaxTicketCount()).isEqualTo(performance.getMaxTicketCount());
		assertThat(detail.getDiscountInfo()).isEqualTo(performance.getDiscountInfo());

		// 회차 정보 검증
		assertThat(detail.getSchedules()).hasSize(1);
		PerformanceScheduleResponseDto schedule = detail.getSchedules().get(0);

		// 여기서 기본적으로 저장된 스케줄 값이 일치하는지 확인
		assertThat(schedule.getStartAt().toString()).isEqualTo(
			performance.getSchedules().get(0).getStartAt().toString());
		assertThat(schedule.getEndAt().toString()).isEqualTo(performance.getSchedules().get(0).getEndAt().toString());
		assertThat(schedule.getSaleStartAt().toString()).isEqualTo(
			performance.getSchedules().get(0).getSaleStartAt().toString());
		assertThat(schedule.getSaleEndAt().toString()).isEqualTo(
			performance.getSchedules().get(0).getSaleEndAt().toString());
		assertThat(schedule.getPerformanceScheduleId()).isNotNull();
		assertThat(schedule.getPerformanceHallId().toString()).isEqualTo("11111111-2222-3333-4444-555555555555");

		// 좌석 가격 정보 검증
		List<SeatPriceResponseDto> seatPrices = schedule.getSeatPrices();
		assertThat(seatPrices).hasSize(2);

		// 첫 번째 좌석의 값 비교
		SeatPriceResponseDto vipSeat = seatPrices.get(0);
		assertThat(vipSeat.getPerformanceSeatPriceId()).isNotNull();
		assertThat(vipSeat.getSeatType().name()).isEqualTo(SeatType.VIP.name());
		assertThat(vipSeat.getPrice()).isEqualTo(150000);

		// 두 번째 좌석의 값 비교
		SeatPriceResponseDto rSeat = seatPrices.get(1);
		assertThat(rSeat.getPerformanceSeatPriceId()).isNotNull();
		assertThat(rSeat.getSeatType().name()).isEqualTo(SeatType.R.name());
		assertThat(rSeat.getPrice()).isEqualTo(120000);
	}

	@Test
	@DisplayName("존재하지 않는 ID로 공연 상세 조회 시 예외가 발생해야 한다 (Service 테스트)")
	void getDetail_shouldFail_whenPerformanceIdNotExist() {
		// given
		UUID fakeId = UUID.randomUUID();

		// when & then
		assertThatThrownBy(() -> performanceService.getDetail(fakeId))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("공연 삭제 성공 시 deletedAt, deletedBy가 정상적으로 반영되어야 한다")
	void deletePerformance_shouldSoftDelete() throws Exception {
		// given
		CreateRequestDto requestDto = createValidRequestDto();
		Performance performance = Performance.create(requestDto);
		performanceJpaRepository.save(performance);

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

	@Test
	@DisplayName("공연 실패 케이스 삭제하려는 ID가 없는 경우")
	void deletePerformance_shouldFail_whenIdIsNull() {

		// given
		UUID deletedBy = UUID.randomUUID();

		// when & then
		assertThatThrownBy(() -> performanceService.delete(null, deletedBy))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("삭제할 수 없는 아이디입니다");
	}
}