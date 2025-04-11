package com.taken_seat.performance_service.performancehall.application.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDto {

	private UUID performanceHallId;
	private String name;
	private Integer totalSeats;
}
