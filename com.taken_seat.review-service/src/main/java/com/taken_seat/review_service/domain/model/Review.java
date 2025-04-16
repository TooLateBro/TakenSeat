package com.taken_seat.review_service.domain.model;

import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.dto.request.ReviewRegisterReqDto;
import com.taken_seat.review_service.application.dto.request.ReviewUpdateReqDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_review")
public class Review extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@Column(columnDefinition = "BINARY(16)", nullable = false)
	private UUID performanceId;

	@Column(columnDefinition = "BINARY(16)", nullable = false)
	private UUID performanceScheduleId;

	@Column(columnDefinition = "BINARY(16)", nullable = false)
	private UUID authorId;

	@Column(nullable = false)
	private String authorEmail;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 100)
	private String content;

	@Column(nullable = false)
	private short rating;

	@Column(nullable = false)
	private Integer likeCount;

	public static Review create(ReviewRegisterReqDto reviewRegisterReqDto, AuthenticatedUser authenticatedUser) {

		Review review = Review.builder()
			.performanceId(reviewRegisterReqDto.getPerformanceId())
			.performanceScheduleId(reviewRegisterReqDto.getPerformanceScheduleId())
			.authorId(authenticatedUser.getUserId())
			.authorEmail(authenticatedUser.getEmail())
			.title(reviewRegisterReqDto.getTitle())
			.content(reviewRegisterReqDto.getContent())
			.rating(reviewRegisterReqDto.getRating())
			.likeCount(0)
			.build();

		review.prePersist(authenticatedUser.getUserId());

		return review;
	}

	public void update(ReviewUpdateReqDto reviewUpdateReqDto, AuthenticatedUser authenticatedUser) {
		this.title = reviewUpdateReqDto.getTitle();
		this.content = reviewUpdateReqDto.getContent();
		this.rating = reviewUpdateReqDto.getRating();
		this.preUpdate(authenticatedUser.getUserId());
	}

	public void updateLikeCount(int i) {
		int newCnt = this.likeCount + i;
		if (newCnt < 0) {
			throw new ReviewException(ResponseCode.INVALID_LIKE_COUNT);
		}
		this.likeCount = newCnt;
	}
}
