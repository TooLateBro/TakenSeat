package com.taken_seat.review_service.application.dto.service;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSearchDto {

	private UUID performance_id;
	private String q;
	private String category;
	private Integer page;
	private Integer size;
	private String sort;
	private String order;
}
