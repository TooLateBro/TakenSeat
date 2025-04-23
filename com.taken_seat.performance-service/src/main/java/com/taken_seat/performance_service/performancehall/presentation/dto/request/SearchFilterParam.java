package com.taken_seat.performance_service.performancehall.presentation.dto.request;

public record SearchFilterParam(
	String name,
	String address,
	Integer maxSeats,
	Integer minSeats
) {
}
