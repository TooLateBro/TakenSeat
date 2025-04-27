package com.taken_seat.review_service.application.dto.controller.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRegisterReqDto {

	private UUID performanceId;

	private UUID performanceScheduleId;

	private String title;

	private String content;

	private short rating;
}
