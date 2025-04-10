package com.taken_seat.booking_service.ticket.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "performance-service", path = "/api/v1/performances")
public interface PerformanceClient {
}