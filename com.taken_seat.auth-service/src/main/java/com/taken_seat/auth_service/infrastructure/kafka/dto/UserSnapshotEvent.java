package com.taken_seat.auth_service.infrastructure.kafka.dto;

import java.util.List;
import java.util.UUID;

public record UserSnapshotEvent(
    List<UUID> userIds
) {}