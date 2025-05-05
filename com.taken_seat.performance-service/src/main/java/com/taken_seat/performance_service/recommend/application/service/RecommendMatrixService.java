package com.taken_seat.performance_service.recommend.application.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RecommendMatrixService {

	private final Map<UUID, Set<UUID>> userPerformanceMap = new ConcurrentHashMap<>();

	public void registerBooking(UUID userId, UUID performanceId, int ticketCount) {

		userPerformanceMap
			.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet())
			.add(performanceId);
	}

	public boolean hasBooked(UUID userId, UUID performanceId) {
		return userPerformanceMap
			.getOrDefault(userId, Set.of())
			.contains(performanceId);
	}
}
