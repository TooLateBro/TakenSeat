package com.taken_seat.performance_service.performancehall.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilterParam {

	private String name;
	private String address;
	private Integer minSeats;
	private Integer maxSeats;
}
