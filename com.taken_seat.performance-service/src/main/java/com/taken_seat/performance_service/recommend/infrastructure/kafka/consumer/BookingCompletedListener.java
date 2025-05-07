package com.taken_seat.performance_service.recommend.infrastructure.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.message.BookingCompletedMessage;
import com.taken_seat.performance_service.recommend.application.command.RecommendRegisterCommand;
import com.taken_seat.performance_service.recommend.application.service.RecommendMatrixService;
import com.taken_seat.performance_service.recommend.infrastructure.redis.RecommendationCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class BookingCompletedListener {

	private final RecommendMatrixService recommendMatrixService;
	private final RecommendationCacheService recommendationCacheService;

	@KafkaListener(
		topics = "booking.completed.v1",
		groupId = "performance-recommend",
		containerFactory = "bookingKafkaListenerContainerFactory"
	)
	public void listen(BookingCompletedMessage message) {

		log.info("[Recommend] Booking Kafka 메시지 수신 - 시작 - userId={}, performanceId={}, ticketCount={}",
			message.userId(), message.performanceId(), message.ticketCount());

		RecommendRegisterCommand command = new RecommendRegisterCommand(
			message.userId(),
			message.performanceId(),
			message.ticketCount()
		);

		recommendMatrixService.registerBooking(
			command.userId(),
			command.performanceId(),
			command.ticketCount()
		);
		log.info("[Recommend] 매트릭스 등록 - 완료 - userId={}, performanceId={}",
			command.userId(), command.performanceId());

		recommendationCacheService.evict(command.userId());
		log.info("[Recommend] 추천 캐시 삭제 - 완료 - key={}", command.userId());
	}
}