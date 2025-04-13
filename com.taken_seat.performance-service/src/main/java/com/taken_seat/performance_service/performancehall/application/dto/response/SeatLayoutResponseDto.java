package com.taken_seat.performance_service.performancehall.application.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatLayoutResponseDto {

	private List<SeatDto> seats;
}
