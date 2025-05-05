package com.taken_seat.performance_service.recommend.infrastructure.scheduler;

import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.recommend.infrastructure.kafka.consumer.UserSnapshotListener;
import com.taken_seat.performance_service.recommend.infrastructure.kafka.dto.RecommendRequestMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableScheduling
public class RecommendRequestScheduler {

	private static final String TOPIC_RECOMMEND_REQUEST = "recommend.request.v1";
	private static final String CRON_DAILY_6AM = "0 0 6 * * *";

	private final UserSnapshotListener userSnapshotListener;
	private final KafkaTemplate<String, RecommendRequestMessage> kafkaTemplate;

	/**
	 * 매일 오전 6시 정각에, 스냅샷 캐시에서 읽은 활성 사용자 리스트로
	 * recommend.request.v1 메시지를 발행합니다.
	 */
	@Scheduled(cron = CRON_DAILY_6AM)
	public void scheduleDailyRecommendRequests() {

		List<UUID> userIds = userSnapshotListener.getAllActiveUserIds();
		log.info("[Scheduler] {}명 스냅샷 기반 추천 요청 시작", userIds.size());

		for (UUID userId : userIds) {
			kafkaTemplate.send(
				TOPIC_RECOMMEND_REQUEST,
				new RecommendRequestMessage(userId)
			);
		}

		log.info("[Scheduler] 스냅샷 기반 추천 요청 발행 완료");
	}
}
