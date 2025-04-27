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
public class ReviewDto {

	private UUID reviewId;        // 수정할 때 필요한 ID (등록 시는 null)

	// 등록할 때 필요한 공연 ID (수정시에는 필요없을 수 있음)
	private UUID performanceId;

	// 등록할 때 필요한 공연 스케줄 ID (수정시에는 필요없을 수 있음)
	private UUID performanceScheduleId;

	// 제목
	private String title;

	// 내용
	private String content;

	// 평점 (0~5)
	private short rating;

	private UUID userId;           // 결제 생성자(AuthenticatedUser 정보에서)

	private String email;      // 생성자 이메일

	private String role;
}
