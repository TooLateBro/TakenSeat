package com.taken_seat.performance_service.recommend.infrastructure.kafka.consumer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.recommend.infrastructure.kafka.dto.UserSnapshotEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserSnapshotListener {

	private final AtomicReference<List<UUID>> activeUserIds = new AtomicReference<>(List.of());

	@KafkaListener(
		topics = "user.snapshot.v1",
		containerFactory = "userSnapshotListenerContainerFactory"
	)
	public void onUserSnapshot(UserSnapshotEvent event) {
		List<UUID> ids = Collections.unmodifiableList(event.userIds());
		activeUserIds.set(ids);
		log.info("[UserSnapshotListener] 스냅샷 수신: {}명 사용자", ids.size());
	}

	/** 스케줄러가 사용할 활성 사용자 ID 리스트 조회 */
	public List<UUID> getAllActiveUserIds() {
		return activeUserIds.get();
	}
}
