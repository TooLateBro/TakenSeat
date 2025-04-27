// package com.taken_seat.booking_service.infrastructure.service;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.taken_seat.booking_service.booking.application.dto.request.BookingCreateRequest;
// import com.taken_seat.booking_service.booking.application.service.BookingService;
// import com.taken_seat.common_service.dto.AuthenticatedUser;
//
// @SpringBootTest
// @Transactional
// class ConcurrencyTest {
//
// 	@Autowired
// 	private BookingService bookingService;
//
// 	private final UUID scheduleSeatId = UUID.randomUUID();
//
// 	@Test
// 	@DisplayName("동시 요청시 하나만 성공")
// 	void test() throws InterruptedException {
// 		int threadCount = 10;
// 		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
// 		CountDownLatch latch = new CountDownLatch(threadCount);
//
// 		List<Future<String>> results = new ArrayList<>();
//
// 		for (int i = 0; i < threadCount; i++) {
// 			int userIndex = i;
// 			results.add(executorService.submit(() -> {
// 				try {
// 					AuthenticatedUser authenticatedUser = new AuthenticatedUser(UUID.randomUUID(), "testEmail",
// 						"testRole");
//
// 					BookingCreateRequest request = BookingCreateRequest.builder()
// 						.scheduleSeatId(scheduleSeatId)
// 						.build();
//
// 					bookingService.createBooking(authenticatedUser, request);
// 					return "SUCCESS: 사용자 " + userIndex;
// 				} catch (Exception e) {
// 					return "FAIL: 사용자 " + userIndex + " -> " + e.getMessage();
// 				} finally {
// 					latch.countDown();
// 				}
// 			}));
// 		}
//
// 		latch.await();
//
// 		long successCount = results.stream()
// 			.map(f -> {
// 				try {
// 					return f.get();
// 				} catch (Exception e) {
// 					return "FAIL";
// 				}
// 			})
// 			.peek(System.out::println)
// 			.filter(msg -> msg.startsWith("SUCCESS"))
// 			.count();
//
// 		assertEquals(successCount, 1);
// 	}
// }