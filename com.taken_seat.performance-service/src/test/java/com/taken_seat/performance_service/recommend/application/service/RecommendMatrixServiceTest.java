package com.taken_seat.performance_service.recommend.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RecommendMatrixServiceTest {

	private static final Logger log = LoggerFactory.getLogger(RecommendMatrixServiceTest.class);

	private RecommendMatrixService service;
	private UUID user1, user2, user3;
	private UUID perfA, perfB, perfC, perfD;

	@BeforeEach
	void setUp() {
		service = new RecommendMatrixService();
		user1 = UUID.randomUUID();
		user2 = UUID.randomUUID();
		user3 = UUID.randomUUID();

		perfA = UUID.randomUUID();
		perfB = UUID.randomUUID();
		perfC = UUID.randomUUID();
		perfD = UUID.randomUUID();

		log.info("테스트 데이터 초기화 시작");

		// 유저1: A, B
		service.registerBooking(user1, perfA, 1);
		service.registerBooking(user1, perfB, 1);
		log.info("user1 공연 등록: A={}, B={}", perfA, perfB);

		// 유저2: A, B, C
		service.registerBooking(user2, perfA, 1);
		service.registerBooking(user2, perfB, 1);
		service.registerBooking(user2, perfC, 1);
		log.info("user2 공연 등록: A={}, B={}, C={}", perfA, perfB, perfC);

		// 유저3: D
		service.registerBooking(user3, perfD, 1);
		log.info("user3 공연 등록: D={}", perfD);
	}

	@Test
	@DisplayName("user1에 대한 추천은 user2 기반으로 C가 나와야 한다")
	void testRecommendForUser1() {
		log.info("[TEST] user1 추천 테스트 시작");
		List<UUID> result = service.recommendFor(user1, 3);
		log.info("추천 결과: {}", result);

		assertTrue(result.contains(perfC), "user1은 perfC를 추천받아야 함");
		assertFalse(result.contains(perfA), "이미 본 공연은 추천에 없어야 함");
		assertFalse(result.contains(perfB));
	}

	@Test
	@DisplayName("user3은 고립된 유저이므로 추천 결과 없음")
	void testRecommendForUser3() {
		log.info("[TEST] user3 고립 추천 테스트 시작");
		List<UUID> result = service.recommendFor(user3, 3);
		log.info("추천 결과: {}", result);

		assertTrue(result.isEmpty(), "연관 유저 없음 → 추천 결과 없음");
	}
}