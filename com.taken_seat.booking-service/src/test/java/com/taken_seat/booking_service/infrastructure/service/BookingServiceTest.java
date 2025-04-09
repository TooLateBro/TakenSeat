package com.taken_seat.booking_service.infrastructure.service;

import static org.junit.jupiter.api.Assertions.*;
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
import com.taken_seat.booking_service.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.application.dto.response.BookingReadResponse;
import com.taken_seat.booking_service.domain.Booking;
import com.taken_seat.booking_service.domain.repository.BookingRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("단건 조회 테스트")
	void test1() throws Exception {
		Booking booking = Booking.builder()
			.userId(UUID.randomUUID())
			.performanceScheduleId(UUID.randomUUID())
			.seatId(UUID.randomUUID())
			.build();

		Booking saved = bookingRepository.saveAndFlush(booking);

		MvcResult mvcResult = mockMvc.perform(get("/api/v1/bookings/" + saved.getId()))
			.andExpect(status().isOk())
			.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		BookingReadResponse response = objectMapper.readValue(contentAsString, BookingReadResponse.class);

		assertEquals(response.getId(), saved.getId());
	}

	@Test
	@DisplayName("페이지 조회 테스트")
	void test2() throws Exception {
		UUID userId = UUID.randomUUID();

		Booking booking1 = Booking.builder()
			.userId(userId)
			.performanceScheduleId(UUID.randomUUID())
			.seatId(UUID.randomUUID())
			.build();

		Booking booking2 = Booking.builder()
			.userId(userId)
			.performanceScheduleId(UUID.randomUUID())
			.seatId(UUID.randomUUID())
			.build();

		List<Booking> list = List.of(booking1, booking2);
		bookingRepository.saveAllAndFlush(list);

		MvcResult mvcResult = mockMvc.perform(get("/api/v1/bookings").param("userId", userId.toString()))
			.andExpect(status().isOk())
			.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		BookingPageResponse response = objectMapper.readValue(contentAsString, BookingPageResponse.class);

		assertEquals(response.getTotalElements(), list.size());
	}

	@Test
	@DisplayName("예매 취소 테스트")
	void test3() throws Exception {
		Booking booking = Booking.builder()
			.userId(UUID.randomUUID())
			.performanceScheduleId(UUID.randomUUID())
			.seatId(UUID.randomUUID())
			.build();

		Booking saved = bookingRepository.saveAndFlush(booking);

		mockMvc.perform(patch("/api/v1/bookings/" + saved.getId()))
			.andExpect(status().isNoContent());

		assertNotNull(saved.getCanceledAt());
	}

	@Test
	@DisplayName("예매 삭제 테스트")
	void test4() throws Exception {
		Booking booking = Booking.builder()
			.userId(UUID.randomUUID())
			.performanceScheduleId(UUID.randomUUID())
			.seatId(UUID.randomUUID())
			.canceledAt(LocalDateTime.now())
			.build();

		Booking saved = bookingRepository.saveAndFlush(booking);

		mockMvc.perform(delete("/api/v1/bookings/" + saved.getId()))
			.andExpect(status().isNoContent());
	}

}