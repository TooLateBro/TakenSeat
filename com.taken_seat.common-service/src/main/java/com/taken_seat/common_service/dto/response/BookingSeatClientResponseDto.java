package com.taken_seat.common_service.dto.response;

public record BookingSeatClientResponseDto(
	Integer price,
	boolean reserved,
	String reason
) {
}
