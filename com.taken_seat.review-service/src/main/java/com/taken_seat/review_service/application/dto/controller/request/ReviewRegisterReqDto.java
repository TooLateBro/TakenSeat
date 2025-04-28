package com.taken_seat.review_service.application.dto.controller.request;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRegisterReqDto {

	@NotNull(message = "공연 ID는 필수 입력값입니다.")
	private UUID performanceId;

	@NotNull(message = "공연 회차 ID는 필수 입력값입니다.")
	private UUID performanceScheduleId;

	@NotBlank(message = "제목은 필수 입력값입니다.")
	private String title;

	@NotBlank(message = "내용은 필수 입력값입니다.")
	private String content;

	@NotNull(message = "평점은 필수 입력값입니다.")
	@Min(value = 1, message = "평점은 최소 1점 이상이어야 합니다.")
	@Max(value = 5, message = "평점은 최대 5점 이하여야 합니다.")
	private short rating;
}
