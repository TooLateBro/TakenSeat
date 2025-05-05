package com.taken_seat.performance_service.recommend.infrastructure.kafka.dto;

import java.util.List;
import java.util.UUID;

public record UserSnapshotEvent(
	List<UUID> userIds
) {
}
