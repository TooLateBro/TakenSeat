package com.taken_seat.booking_service.ticket.infrastructure.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketPageResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketReadResponse;
import com.taken_seat.booking_service.ticket.domain.Ticket;
import com.taken_seat.booking_service.ticket.domain.repository.TicketRepository;
import com.taken_seat.common_service.dto.ApiResponseData;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TicketServiceTest {

	private final UUID userId = UUID.randomUUID();

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("단건 조회 테스트")
	void test1() throws Exception {
		Ticket ticket = Ticket.builder()
			.userId(userId)
			.bookingId(UUID.randomUUID())
			.performanceScheduleId(UUID.randomUUID())
			.seatId(UUID.randomUUID())
			.build();

		Ticket saved = ticketRepository.saveAndFlush(ticket);

		MvcResult mvcResult = mockMvc.perform(
				get("/api/v1/tickets/" + saved.getId())
					.header("X-User-Id", userId.toString()))
			.andExpect(status().isOk())
			.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		ApiResponseData<TicketReadResponse> response = objectMapper.readValue(contentAsString,
			new TypeReference<ApiResponseData<TicketReadResponse>>() {
			});

		assertEquals(response.body().getId(), saved.getId());
	}

	@Test
	@DisplayName("페이지 조회 테스트")
	void test2() throws Exception {
		Ticket ticket1 = Ticket.builder()
			.userId(userId)
			.bookingId(UUID.randomUUID())
			.performanceScheduleId(UUID.randomUUID())
			.seatId(UUID.randomUUID())
			.build();

		Ticket ticket2 = Ticket.builder()
			.userId(userId)
			.bookingId(UUID.randomUUID())
			.performanceScheduleId(UUID.randomUUID())
			.seatId(UUID.randomUUID())
			.build();

		List<Ticket> list = List.of(ticket1, ticket2);
		List<Ticket> savedList = ticketRepository.saveAllAndFlush(list);

		MvcResult mvcResult = mockMvc.perform(
				get("/api/v1/tickets")
					.header("X-User-Id", userId.toString()))
			.andExpect(status().isOk())
			.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
		ApiResponseData<TicketPageResponse> response = objectMapper.readValue(contentAsString,
			new TypeReference<ApiResponseData<TicketPageResponse>>() {
			});

		assertEquals(response.body().getTotalElements(), savedList.size());
	}
}