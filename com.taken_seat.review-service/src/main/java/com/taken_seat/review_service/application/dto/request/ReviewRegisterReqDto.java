package com.taken_seat.review_service.application.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRegisterReqDto {

	private UUID performanceId;

	private UUID performanceScheduleId;

	private String title;

	private String content;
}
