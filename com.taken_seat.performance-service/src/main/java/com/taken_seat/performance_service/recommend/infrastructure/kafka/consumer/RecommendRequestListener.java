package com.taken_seat.performance_service.recommend.infrastructure.kafka.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.recommend.application.service.RecommendQueryService;
import com.taken_seat.performance_service.recommend.infrastructure.kafka.dto.RecommendRequestMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecommendRequestListener {

	private final RecommendQueryService recommendQueryService;

	@KafkaListener(
		topics = "recommend.request.v1",
		containerFactory = "recommendRequestListenerContainerFactory"
	)
	public void onMessage(RecommendRequestMessage message) {
		UUID userId = message.userId();

		log.info("[RecommendRequestListener] 수신된 요청 – topic=recommend.request.v1, userId={}", userId);

		recommendQueryService.recommendFor(userId);

		log.info("[RecommendRequestListener] 추천 계산 & 캐시 저장 완료 – userId={}", userId);
	}
}
