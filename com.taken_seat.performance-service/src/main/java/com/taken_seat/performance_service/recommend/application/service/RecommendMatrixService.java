package com.taken_seat.performance_service.recommend.application.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendMatrixService {

	private final Map<UUID, Set<UUID>> userPerformanceMap = new Object2ObjectOpenHashMap<>();

	public void registerBooking(UUID userId, UUID performanceId, int ticketCount) {

		userPerformanceMap
			.computeIfAbsent(userId, key -> new HashSet<>())
			.add(performanceId);
	}

	public List<UUID> recommendFor(UUID targetUserId, int topN) {

		Set<UUID> targetVector = userPerformanceMap.get(targetUserId);

		if (targetVector == null || targetVector.isEmpty()) {

			log.warn("[Recommend] 추천 불가 - 예매 데이터 없음 - userId={}", targetUserId);
			return List.of();
		}

		Map<UUID, Double> similarityMap = new HashMap<>();

		for (Map.Entry<UUID, Set<UUID>> entry : userPerformanceMap.entrySet()) {
			UUID otherUserId = entry.getKey();
			if (otherUserId.equals(targetUserId))
				continue;

			Set<UUID> otherVector = entry.getValue();
			double similarity =
				cosineSimilarity(targetVector, otherVector);

			if (similarity > 0.0) {
				similarityMap.put(otherUserId, similarity);
			}
		}

		Map<UUID, Double> candidateScores = new HashMap<>();

		for (Map.Entry<UUID, Double> similarityEntry : similarityMap.entrySet()) {
			UUID similarUserId = similarityEntry.getKey();
			double score = similarityEntry.getValue();
			Set<UUID> performances
				= userPerformanceMap.get(similarUserId);
			for (UUID performanceId : performances) {
				if (!targetVector.contains(performanceId)) {
					candidateScores.put(performanceId, candidateScores.getOrDefault(
						performanceId, 0.0) + score
					);
				}
			}
		}

		return candidateScores.entrySet()
			.stream()
			.sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
			.limit(topN)
			.map(Map.Entry::getKey)
			.toList();
	}

	private double cosineSimilarity(Set<UUID> userVectorA, Set<UUID> userVectorB) {
		if (userVectorA.isEmpty() || userVectorB.isEmpty())
			return 0.0;

		Set<UUID> commonPerformances = new HashSet<>(userVectorA);
		commonPerformances.retainAll(userVectorB);

		double dotProduct = commonPerformances.size();
		double magnitudeOfA = Math.sqrt(userVectorA.size());
		double magnitudeOfB = Math.sqrt(userVectorB.size());

		return dotProduct / (magnitudeOfA * magnitudeOfB);
	}
}
