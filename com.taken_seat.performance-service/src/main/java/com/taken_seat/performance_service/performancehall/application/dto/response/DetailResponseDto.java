package com.taken_seat.performance_service.performancehall.application.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailResponseDto {

	private UUID performanceHallId;
	private String name;
	private String address;
	private Integer totalSeats;
	private String description;
	private List<SeatDto> seats;
}
