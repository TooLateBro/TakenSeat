package com.taken_seat.review_service.application.dto.controller.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailResDto {

	private UUID id;

	private UUID performanceId;

	private UUID performanceScheduleId;

	private UUID authorId;

	private String authorEmail;

	private String title;

	private String content;

	private short rating;

	private Integer likeCount;
}
