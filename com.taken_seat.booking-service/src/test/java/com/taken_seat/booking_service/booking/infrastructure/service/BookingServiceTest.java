// package com.taken_seat.booking_service.booking.infrastructure.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.nio.charset.StandardCharsets;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.UUID;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingPageResponse;
// import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingReadResponse;
// import com.taken_seat.booking_service.booking.application.dto.response.BookingPageResponse;
// import com.taken_seat.booking_service.booking.application.dto.response.BookingReadResponse;
// import com.taken_seat.booking_service.booking.domain.Booking;
// import com.taken_seat.booking_service.booking.infrastructure.repository.BookingJpaRepository;
// import com.taken_seat.common_service.dto.ApiResponseData;
// import com.taken_seat.common_service.dto.response.BookingStatusDto;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// class BookingServiceTest {
//
// 	private final UUID userId = UUID.randomUUID();
//
// 	@Autowired
// 	private MockMvc mockMvc;
// 	@Autowired
// 	private BookingJpaRepository bookingJpaRepository;
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@Test
// 	@DisplayName("단건 조회 테스트")
// 	void test1() throws Exception {
//
// 		Booking booking = Booking.builder()
// 			.userId(userId)
// 			.performanceId(UUID.randomUUID())
// 			.performanceScheduleId(UUID.randomUUID())
// 			.scheduleSeatId(UUID.randomUUID())
// 			.build();
//
// 		Booking saved = bookingJpaRepository.saveAndFlush(booking);
//
// 		MvcResult mvcResult = mockMvc.perform(
// 				get("/api/v1/bookings/" + saved.getId())
// 					.header("X-User-Id", userId.toString()))
// 			.andExpect(status().isOk())
// 			.andReturn();
//
// 		String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
// 		ApiResponseData<BookingReadResponse> response = objectMapper.readValue(contentAsString,
// 			new TypeReference<ApiResponseData<BookingReadResponse>>() {
// 			});
//
// 		assertEquals(response.body().getId(), saved.getId());
// 	}
//
// 	@Test
// 	@DisplayName("페이지 조회 테스트")
// 	void test2() throws Exception {
//
// 		Booking booking1 = Booking.builder()
// 			.userId(userId)
// 			.performanceId(UUID.randomUUID())
// 			.performanceScheduleId(UUID.randomUUID())
// 			.scheduleSeatId(UUID.randomUUID())
// 			.build();
//
// 		Booking booking2 = Booking.builder()
// 			.userId(userId)
// 			.performanceId(UUID.randomUUID())
// 			.performanceScheduleId(UUID.randomUUID())
// 			.scheduleSeatId(UUID.randomUUID())
// 			.build();
//
// 		List<Booking> list = List.of(booking1, booking2);
// 		List<Booking> savedList = bookingJpaRepository.saveAllAndFlush(list);
//
// 		MvcResult mvcResult = mockMvc.perform(
// 				get("/api/v1/bookings")
// 					.param("userId", userId.toString())
// 					.header("X-User-Id", userId.toString()))
// 			.andExpect(status().isOk())
// 			.andReturn();
//
// 		String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
// 		ApiResponseData<BookingPageResponse> response = objectMapper.readValue(contentAsString,
// 			new TypeReference<ApiResponseData<BookingPageResponse>>() {
// 			});
//
// 		assertEquals(response.body().getTotalElements(), savedList.size());
// 	}
//
// 	@Test
// 	@DisplayName("예매 취소 테스트")
// 	void test3() throws Exception {
//
// 		Booking booking = Booking.builder()
// 			.userId(userId)
// 			.performanceId(UUID.randomUUID())
// 			.performanceScheduleId(UUID.randomUUID())
// 			.scheduleSeatId(UUID.randomUUID())
// 			.build();
//
// 		Booking saved = bookingJpaRepository.saveAndFlush(booking);
//
// 		mockMvc.perform(
// 				patch("/api/v1/bookings/" + saved.getId())
// 					.header("X-User-Id", userId.toString()))
// 			.andExpect(status().isNoContent());
//
// 		assertNotNull(saved.getCanceledAt());
// 	}
//
// 	@Test
// 	@DisplayName("예매 삭제 테스트")
// 	void test4() throws Exception {
//
// 		Booking booking = Booking.builder()
// 			.userId(userId)
// 			.performanceId(UUID.randomUUID())
// 			.performanceScheduleId(UUID.randomUUID())
// 			.scheduleSeatId(UUID.randomUUID())
// 			.canceledAt(LocalDateTime.now())
// 			.build();
//
// 		Booking saved = bookingJpaRepository.saveAndFlush(booking);
//
// 		mockMvc.perform(
// 				delete("/api/v1/bookings/" + saved.getId())
// 					.header("X-User-Id", userId.toString()))
// 			.andExpect(status().isNoContent());
// 	}
//
// 	@Test
// 	@DisplayName("어드민 단건 조회 테스트 - 다른 사람 예매 조회 성공")
// 	void test5() throws Exception {
//
// 		UUID randomUserId = UUID.randomUUID();
// 		Booking booking = Booking.builder()
// 			.userId(randomUserId)
// 			.performanceId(UUID.randomUUID())
// 			.performanceScheduleId(UUID.randomUUID())
// 			.scheduleSeatId(UUID.randomUUID())
// 			.canceledAt(LocalDateTime.now())
// 			.build();
//
// 		Booking saved = bookingJpaRepository.saveAndFlush(booking);
//
// 		MvcResult mvcResult = mockMvc.perform(
// 				get("/api/v1/admin/bookings/" + saved.getId())
// 					.header("X-User-Id", userId.toString())
// 					.header("X-Role", "MASTER"))
// 			.andExpect(status().isOk())
// 			.andReturn();
//
// 		String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
// 		ApiResponseData<AdminBookingReadResponse> response = objectMapper.readValue(contentAsString,
// 			new TypeReference<ApiResponseData<AdminBookingReadResponse>>() {
// 			});
//
// 		assertEquals(randomUserId, response.body().getUserId());
// 	}
//
// 	@Test
// 	@DisplayName("어드민 페이지 조회 테스트 - 다른 사람 예매 조회 성공")
// 	void test6() throws Exception {
//
// 		UUID randomUserId = UUID.randomUUID();
// 		Booking booking1 = Booking.builder()
// 			.userId(randomUserId)
// 			.performanceId(UUID.randomUUID())
// 			.performanceScheduleId(UUID.randomUUID())
// 			.scheduleSeatId(UUID.randomUUID())
// 			.canceledAt(LocalDateTime.now())
// 			.build();
//
// 		Booking booking2 = Booking.builder()
// 			.userId(randomUserId)
// 			.performanceId(UUID.randomUUID())
// 			.performanceScheduleId(UUID.randomUUID())
// 			.scheduleSeatId(UUID.randomUUID())
// 			.canceledAt(LocalDateTime.now())
// 			.build();
//
// 		List<Booking> list = List.of(booking1, booking2);
// 		List<Booking> savedList = bookingJpaRepository.saveAllAndFlush(list);
//
// 		MvcResult mvcResult = mockMvc.perform(
// 				get("/api/v1/admin/bookings")
// 					.header("X-User-Id", userId.toString())
// 					.header("X-Role", "MASTER"))
// 			.andExpect(status().isOk())
// 			.andReturn();
//
// 		String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
// 		ApiResponseData<AdminBookingPageResponse> response = objectMapper.readValue(contentAsString,
// 			new TypeReference<ApiResponseData<AdminBookingPageResponse>>() {
// 			});
//
// 		assertEquals(response.body().getTotalElements(), savedList.size());
// 	}
//
// 	@Test
// 	@DisplayName("클라이언트 메서드 테스트")
// 	void test7() throws Exception {
// 		UUID performanceId = UUID.randomUUID();
// 		Booking booking = Booking.builder()
// 			.userId(userId)
// 			.performanceId(performanceId)
// 			.performanceScheduleId(UUID.randomUUID())
// 			.scheduleSeatId(UUID.randomUUID())
// 			.canceledAt(LocalDateTime.now())
// 			.build();
//
// 		bookingJpaRepository.saveAndFlush(booking);
//
// 		MvcResult mvcResult = mockMvc.perform(
// 				get("/api/v1/bookings/%s/%s/status".formatted(userId, performanceId)))
// 			.andExpect(status().isOk())
// 			.andReturn();
//
// 		String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
// 		ApiResponseData<BookingStatusDto> response = objectMapper.readValue(contentAsString,
// 			new TypeReference<ApiResponseData<BookingStatusDto>>() {
// 			});
//
// 		assertEquals("PENDING", response.body().status());
// 	}
// }