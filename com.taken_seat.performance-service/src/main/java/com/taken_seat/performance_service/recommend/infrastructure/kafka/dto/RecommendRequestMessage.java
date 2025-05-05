package com.taken_seat.performance_service.recommend.infrastructure.kafka.dto;

import java.util.UUID;

public record RecommendRequestMessage(
	UUID userId
) {
}
