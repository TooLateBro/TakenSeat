// package com.taken_seat.auth_service;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.taken_seat.common_service.message.KafkaUserInfoMessage;
// import org.junit.jupiter.api.Test;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;
//
// import java.util.UUID;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// class UserToCouponPublisherControllerTest {
//
//     private static final Logger log = LoggerFactory.getLogger(UserToCouponPublisherControllerTest.class);
//     @Autowired
//     private MockMvc mockMvc;
//
//     @Autowired
//     private ObjectMapper objectMapper;
//
//     @Test
//     public void sendUserCouponConcurrencyTest() throws Exception {
//         int threadCount = 50;
//         UUID testCouponId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
//
//         ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//         CountDownLatch latch = new CountDownLatch(threadCount);
//
//         for (int i = 0; i < threadCount; i++) {
//             final int userNumber = i; // 캡처된 로컬 변수
//
//             executorService.submit(() -> {
//                 try {
//                     // 순서를 포함한 userId 생성
//                     String userIdStr = String.format("abcdef01-2345-6789-abcd-%012d", userNumber);
//                     UUID userId = UUID.fromString(userIdStr);
//
//                     KafkaUserInfoMessage message = KafkaUserInfoMessage.builder()
//                             .userId(userId)
//                             .couponId(testCouponId)
//                             .build();
//
//                     String requestBody = objectMapper.writeValueAsString(message);
//
//                     mockMvc.perform(post("/api/v1/users/send")
//                                     .contentType(MediaType.APPLICATION_JSON)
//                                     .content(requestBody))
//                             .andExpect(status().isOk());
//
//                     log.info("Send UserId : {}", message.getUserId());
//
//                 } catch (Exception e) {
//                     log.error("Error on Request #{}: {}", userNumber, e.getMessage());
//                     e.printStackTrace();
//                 } finally {
//                     latch.countDown();
//                 }
//             });
//         }
//
//         latch.await();
//     }
// }