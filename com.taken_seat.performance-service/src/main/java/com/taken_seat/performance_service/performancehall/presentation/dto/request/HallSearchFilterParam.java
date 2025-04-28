package com.taken_seat.performance_service.performancehall.presentation.dto.request;

import com.taken_seat.performance_service.performancehall.presentation.dto.request.schema.HallSearchFilterParamSchema;

public record HallSearchFilterParam(
	String name,
	String address,
	Integer maxSeats,
	Integer minSeats
) implements HallSearchFilterParamSchema {
}
