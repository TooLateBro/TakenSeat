package com.taken_seat.review_service.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.client.ReviewClient;
import com.taken_seat.review_service.application.dto.request.ReviewRegisterReqDto;
import com.taken_seat.review_service.application.dto.response.ReviewDetailResDto;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.ReviewRepository;
import com.taken_seat.review_service.infrastructure.client.dto.PerformanceEndTimeDto;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewClient reviewClient;

	public ReviewDetailResDto registerReview(ReviewRegisterReqDto requestDto, UUID userId) {
		// 1. 리뷰 중복 작성 방지
		if (reviewRepository.existsByAuthorIdAndPerformanceId(userId, requestDto.getPerformanceId())) {
			throw new ReviewException(ResponseCode.REVIEW_ALREADY_WRITTEN);
		}

		// 2. 예매 상태 확인
		try {
			String status = reviewClient.getBookingStatus(userId, requestDto.getPerformanceId()).status();
			if (!"COMPLETED".equals(status)) {
				throw new ReviewException(ResponseCode.BOOKING_NOT_COMPLETED);
			}
		} catch (FeignException.Forbidden e) {
			throw new ReviewException(ResponseCode.BOOKING_NOT_COMPLETED);
		}

		// 3. 공연 종료 여부 확인
		try {
			PerformanceEndTimeDto perInfo = reviewClient.getPerformanceEndTime(requestDto.getPerformanceId());
			if (LocalDateTime.now().isBefore(perInfo.endAt())) {
				throw new ReviewException(ResponseCode.EARLY_REVIEW);
			}
		} catch (FeignException.NotFound e) {
			throw new ReviewException(ResponseCode.PERFORMANCE_NOT_FOUND);
		}

		// 5. 리뷰 생성 및 저장
		Review review = Review.create(requestDto, "email", userId);
		reviewRepository.save(review);

		return ReviewDetailResDto.toResponse(review);
	}
}
