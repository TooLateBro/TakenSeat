package com.taken_seat.performance_service.performance.application.scheduler;

import java.util.Map;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performance.application.helper.SeatStatusRedisHelper;
import com.taken_seat.performance_service.performance.application.service.PerformanceClientService;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatStatusSyncScheduler {

	private final SeatStatusRedisHelper seatStatusRedisHelper;
	private final PerformanceClientService performanceClientService;

	@Scheduled(fixedRate = 60_000)
	public void syncSeatStatusFromRedisToDB() {
		log.info("[Performance] Scheduler - Redis 좌석 상태 정합성 점검 시작");

		for (UUID scheduleId : performanceClientService.getAllPerformanceScheduleIds()) {
			Map<UUID, SeatStatus> redisSeatStatuses =
				seatStatusRedisHelper.getAllSeatStatuses(scheduleId);
			Map<UUID, SeatStatus> dbSeatStatuses =
				performanceClientService.getAllSeatStatuses(scheduleId);

			for (Map.Entry<UUID, SeatStatus> entry : redisSeatStatuses.entrySet()) {
				UUID seatId = entry.getKey();
				SeatStatus redisStatus = entry.getValue();
				SeatStatus dbStatus = dbSeatStatuses.get(seatId);

				if (dbStatus == SeatStatus.DISABLED)
					continue;

				if (dbStatus == null || !dbStatus.equals(redisStatus)) {
					log.warn("[Performance] Scheduler - 상태 불일치 감지 - scheduleId={}, seatId={}, Redis={}, DB={}",
						scheduleId, seatId, redisStatus, dbStatus);

					performanceClientService.updateScheduleSeatStatusByKafka(scheduleId, seatId, redisStatus);
				}
			}
		}

		log.info("[Performance] Scheduler - Redis 좌석 상태 정합성 점검 완료");
	}
}
