package com.taken_seat.performance_service.performancehall.presentation.dto.request;

public record HallSearchFilterParam(
	String name,
	String address,
	Integer maxSeats,
	Integer minSeats
) {
}
