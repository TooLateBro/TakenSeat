package com.taken_seat.auth_service.scheduler;

import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.infrastructure.kafka.dto.UserSnapshotEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableScheduling
@Component
public class UserSnapshotScheduler {
  
	private final UserRepository userRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	private static final String TOPIC_USER_SNAPSHOT = "user.snapshot.v1";
	private static final String CRON_DAILY_6AM = "0 0 6 * * *";

	/**
	 * 매일 오전 6시(서버 로컬 타임존)에 전체 활성 사용자 스냅샷을
	 * Kafka 토픽 "user.snapshot.v1" 으로 발행합니다.
	 */
	@Scheduled(cron = CRON_DAILY_6AM)
	public void publishDailyUserSnapshot() {
		List<UUID> userIds = userRepository.findAllIdsByDeletedAtIsNull();
		kafkaTemplate.send(TOPIC_USER_SNAPSHOT, new UserSnapshotEvent(userIds));
	}
}