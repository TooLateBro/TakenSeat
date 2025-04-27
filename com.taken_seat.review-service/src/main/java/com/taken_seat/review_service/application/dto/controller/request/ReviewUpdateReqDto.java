package com.taken_seat.review_service.application.dto.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateReqDto {

	private String title;

	private String content;

	private short rating;
}
