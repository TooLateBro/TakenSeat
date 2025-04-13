package com.taken_seat.review_service.infrastructure.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.client.ReviewClient;
import com.taken_seat.review_service.application.dto.request.ReviewRegisterReqDto;
import com.taken_seat.review_service.application.dto.request.ReviewUpdateReqDto;
import com.taken_seat.review_service.application.dto.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.service.ReviewService;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.ReviewQuerydslRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;
import com.taken_seat.review_service.infrastructure.client.dto.PerformanceEndTimeDto;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewQuerydslRepository reviewQuerydslRepository;
	private final ReviewClient reviewClient;

	@Override
	@CachePut(cacheNames = "reviewCache", key = "#result.id")
	public ReviewDetailResDto registerReview(ReviewRegisterReqDto requestDto, AuthenticatedUser authenticatedUser) {
		// 1. 리뷰 중복 작성 방지
		if (reviewRepository.existsByAuthorIdAndPerformanceId(authenticatedUser.getUserId(),
			requestDto.getPerformanceId())) {
			throw new ReviewException(ResponseCode.REVIEW_ALREADY_WRITTEN);
		}

		// 2. 예매 상태 확인
		try {
			String status = reviewClient.getBookingStatus(authenticatedUser.getUserId(), requestDto.getPerformanceId())
				.status();
			if (!"COMPLETED".equals(status)) {
				throw new ReviewException(ResponseCode.BOOKING_NOT_COMPLETED);
			}
		} catch (FeignException.Forbidden e) {
			throw new ReviewException(ResponseCode.BOOKING_NOT_COMPLETED);
		}

		// 3. 공연 종료 여부 확인
		try {
			PerformanceEndTimeDto perInfo = reviewClient.getPerformanceEndTime(requestDto.getPerformanceId(),
				requestDto.getPerformanceScheduleId());
			if (LocalDateTime.now().isBefore(perInfo.endAt())) {
				throw new ReviewException(ResponseCode.EARLY_REVIEW);
			}
		} catch (FeignException.NotFound e) {
			throw new ReviewException(ResponseCode.PERFORMANCE_NOT_FOUND);
		}

		// 4. 리뷰 생성 및 저장
		Review review = Review.create(requestDto, authenticatedUser);
		reviewRepository.save(review);

		return ReviewDetailResDto.toResponse(review);
	}

	@Override
	@Transactional(readOnly = true)
	@CachePut(cacheNames = "reviewCache", key = "#id")
	public ReviewDetailResDto getReviewDetail(UUID id) {

		Review review = reviewRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new ReviewException(ResponseCode.REVIEW_NOT_FOUND));

		return ReviewDetailResDto.toResponse(review);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "reviewSearchCache", key = "#q + '-' + #category + '-' + #page + '-' + #size")
	public PageReviewResponseDto searchReview(String q, String category, int page, int size, String sort,
		String order) {

		Page<Review> reviewPages = reviewQuerydslRepository.search(q, category, page, size, sort, order);

		Page<ReviewDetailResDto> reviewDetailResDtoPages = reviewPages.map(ReviewDetailResDto::toResponse);

		return PageReviewResponseDto.toResponse(reviewDetailResDtoPages);
	}

	@Override
	@CachePut(cacheNames = "reviewCache", key = "#id")
	@CacheEvict(cacheNames = "reviewSearchCache", allEntries = true)
	public ReviewDetailResDto updateReview(UUID id, ReviewUpdateReqDto reviewUpdateReqDto,
		AuthenticatedUser authenticatedUser) {

		Review review = reviewRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new ReviewException(ResponseCode.REVIEW_NOT_FOUND));

		validateAccessAuthority(review, authenticatedUser);

		review.update(reviewUpdateReqDto, authenticatedUser);

		return ReviewDetailResDto.toResponse(review);
	}

	@Override
	@Caching(evict = {
		@CacheEvict(cacheNames = "reviewCache", key = "#id"),
		@CacheEvict(cacheNames = "reviewSearchCache", key = "#id")
	})
	public void deleteReview(UUID id, AuthenticatedUser authenticatedUser) {

		Review review = reviewRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new ReviewException(ResponseCode.REVIEW_NOT_FOUND));

		validateAccessAuthority(review, authenticatedUser);

		review.delete(authenticatedUser.getUserId());
	}

	private void validateAccessAuthority(Review review, AuthenticatedUser authenticatedUser) {
		boolean isAuthor = review.getAuthorId().equals(authenticatedUser.getUserId());
		boolean isMaster = "MASTER".equals(authenticatedUser.getRole());

		if (!(isAuthor || isMaster)) {
			throw new ReviewException(ResponseCode.FORBIDDEN_REVIEW_ACCESS);
		}
	}
}
