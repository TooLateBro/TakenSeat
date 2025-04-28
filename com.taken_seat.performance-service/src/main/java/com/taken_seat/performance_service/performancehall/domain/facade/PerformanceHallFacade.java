package com.taken_seat.performance_service.performancehall.domain.facade;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performancehall.application.dto.command.SeatTemplateInfo;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;

public interface PerformanceHallFacade {

	PerformanceHall getByPerformanceHallId(UUID performanceHallId);

	List<SeatTemplateInfo> getSeatTemplate(UUID performanceHallId);
}
