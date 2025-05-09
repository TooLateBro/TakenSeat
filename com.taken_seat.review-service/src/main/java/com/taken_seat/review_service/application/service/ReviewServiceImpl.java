package com.taken_seat.review_service.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.client.ReviewClient;
import com.taken_seat.review_service.application.dto.controller.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.controller.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.dto.service.ReviewDto;
import com.taken_seat.review_service.application.dto.service.ReviewSearchDto;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.CustomReviewQuerydslRepository;
import com.taken_seat.review_service.domain.repository.RedisRatingRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;
import com.taken_seat.review_service.infrastructure.client.dto.PerformanceEndTimeDto;
import com.taken_seat.review_service.infrastructure.mapper.ReviewMapper;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final CustomReviewQuerydslRepository customReviewQuerydslRepository;
	private final RedisRatingRepository redisRatingRepository;
	private final ReviewClient reviewClient;
	private final ReviewMapper reviewMapper;
	private final ReviewChangeMaker reviewChangeMaker;

	@Override
	@CachePut(cacheNames = "reviewCache", key = "#result.id")
	public ReviewDetailResDto registerReview(ReviewDto reviewDto) {
		// 1. 리뷰 중복 작성 방지
		if (reviewRepository.existsByAuthorIdAndPerformanceIdAndDeletedAtIsNull(reviewDto.getUserId(),
			reviewDto.getPerformanceId())) {
			throw new ReviewException(ResponseCode.REVIEW_ALREADY_WRITTEN);
		}

		// 2. 예매 상태 확인
		try {
			String status = reviewClient.getBookingStatus(reviewDto.getUserId(), reviewDto.getPerformanceId())
				.status();
			if (!"COMPLETED".equals(status)) {
				throw new ReviewException(ResponseCode.BOOKING_NOT_COMPLETED);
			}
		} catch (FeignException.Forbidden e) {
			throw new ReviewException(ResponseCode.BOOKING_NOT_COMPLETED);
		}

		// 3. 공연 종료 여부 확인
		try {
			PerformanceEndTimeDto perInfo = reviewClient.getPerformanceEndTime(reviewDto.getPerformanceId(),
				reviewDto.getPerformanceScheduleId());

			if (LocalDateTime.now().isBefore(perInfo.endAt())) {
				throw new ReviewException(ResponseCode.EARLY_REVIEW);
			}
		} catch (FeignException.NotFound e) {
			throw new ReviewException(ResponseCode.PERFORMANCE_NOT_FOUND);
		}

		// 4. 리뷰 생성 및 저장
		Review review = Review.create(reviewDto);
		reviewRepository.save(review);

		reviewChangeMaker.markPerformanceChanged(review.getPerformanceId());

		return reviewMapper.toResponse(review);
	}

	@Override
	@Transactional(readOnly = true)
	@CachePut(cacheNames = "reviewCache", key = "#reviewId")
	public ReviewDetailResDto getReviewDetail(UUID reviewId) {

		Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
			.orElseThrow(() -> new ReviewException(ResponseCode.REVIEW_NOT_FOUND));

		return reviewMapper.toResponse(review);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "reviewSearchCache",
		key = "#searchDto.performance_id + '-' + #searchDto.q + '-' + #searchDto.category + '-' + #searchDto.page + '-' + #searchDto.size")
	public PageReviewResponseDto searchReview(ReviewSearchDto searchDto) {

		Page<Review> reviewPages = customReviewQuerydslRepository.search(searchDto);

		Page<ReviewDetailResDto> reviewDetailResDtoPages = reviewPages.map(reviewMapper::toResponse);

		return PageReviewResponseDto.toResponse(reviewDetailResDtoPages,
			redisRatingRepository.getAvgRating(searchDto.getPerformance_id()));
	}

	@Override
	@CachePut(cacheNames = "reviewCache", key = "#reviewDto.reviewId")
	@CacheEvict(cacheNames = "reviewSearchCache", allEntries = true)
	public ReviewDetailResDto updateReview(ReviewDto reviewDto) {

		Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewDto.getReviewId())
			.orElseThrow(() -> new ReviewException(ResponseCode.REVIEW_NOT_FOUND));

		validateAccessAuthority(review, reviewDto);

		short originRating = review.getRating();
		short newRating = reviewDto.getRating();

		review.update(reviewDto);

		if (originRating != newRating)
			reviewChangeMaker.markPerformanceChanged(review.getPerformanceId());

		return reviewMapper.toResponse(review);
	}

	@Override
	@Caching(evict = {
		@CacheEvict(cacheNames = "reviewCache", key = "#reviewDto.reviewId"),
		@CacheEvict(cacheNames = "reviewSearchCache", key = "#reviewDto.reviewId")
	})
	public void deleteReview(ReviewDto reviewDto) {

		Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewDto.getReviewId())
			.orElseThrow(() -> new ReviewException(ResponseCode.REVIEW_NOT_FOUND));

		validateAccessAuthority(review, reviewDto);

		review.delete(reviewDto.getUserId());

		reviewChangeMaker.markPerformanceChanged(review.getPerformanceId());

	}

	private void validateAccessAuthority(Review review, ReviewDto reviewDto) {
		boolean isAuthor = review.getAuthorId().equals(reviewDto.getUserId());
		boolean isMaster = "ADMIN".equals(reviewDto.getRole());

		if (!(isAuthor || isMaster)) {
			throw new ReviewException(ResponseCode.FORBIDDEN_REVIEW_ACCESS);
		}
	}
}
