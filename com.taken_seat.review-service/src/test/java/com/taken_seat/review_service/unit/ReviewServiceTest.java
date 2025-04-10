package com.taken_seat.review_service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.client.ReviewClient;
import com.taken_seat.review_service.application.dto.request.ReviewRegisterReqDto;
import com.taken_seat.review_service.application.dto.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.service.ReviewService;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.ReviewRepository;
import com.taken_seat.review_service.infrastructure.client.dto.BookingStatusDto;
import com.taken_seat.review_service.infrastructure.client.dto.PerformanceEndTimeDto;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ReviewClient reviewClient;

	@InjectMocks
	private ReviewService reviewService;

	private UUID testPerformanceId;

	private UUID testReviewId;

	private UUID testAuthorId;

	private Review testReview;

	@BeforeEach
	void setUp() {

		testPerformanceId = UUID.randomUUID();
		testReviewId = UUID.randomUUID();
		testAuthorId = UUID.randomUUID();

		testReview = Review.builder()
			.id(testReviewId)
			.performanceId(testPerformanceId)
			.authorId(testAuthorId)
			.authorName("testUser")
			.title("testReviewTitle")
			.content("testReviewContent")
			.likeCount(0)
			.build();

		testReview.prePersist(UUID.randomUUID());
		reviewRepository.save(testReview);
	}

	@Test
	@DisplayName("리뷰 등록 - SUCCESS")
	void testRegisterReview_success() {
		// Given
		UUID userId = UUID.randomUUID();
		UUID performanceId = UUID.randomUUID();
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(performanceId, "testRegister", "testContent");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(userId, performanceId)).thenReturn(false);
		when(reviewClient.getBookingStatus(userId, performanceId)).thenReturn(new BookingStatusDto("COMPLETED"));
		when(reviewClient.getPerformanceEndTime(performanceId)).thenReturn(
			new PerformanceEndTimeDto(LocalDateTime.now().minusDays(1)));
		when(reviewClient.getUserName(userId)).thenReturn(new UserNameDto("홍길동"));

		// Review 생성 부분
		ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		ReviewDetailResDto result = reviewService.registerReview(requestDto, userId);

		// Then
		assertNotNull(result);
		assertEquals("testRegister", result.getTitle());
		assertEquals("testContent", result.getContent());
		assertEquals("홍길동", result.getAuthorName());

	}

	@Test
	@DisplayName("리뷰 등록 실패 - 해당 공연에 이미 작성된 리뷰가 존재하는 경우")
	void testRegisterReview_fail_alreadyWritten() {
		// Given
		UUID userId = UUID.randomUUID();
		UUID performanceId = UUID.randomUUID();
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(performanceId, "중복", "중복내용");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(userId, performanceId)).thenReturn(true);

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.registerReview(requestDto, userId);
		});

		assertEquals(ResponseCode.REVIEW_ALREADY_WRITTEN.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 예매 상태가 완료되지 않음")
	void testRegisterReview_fail_bookingNotCompleted() {
		// Given
		UUID userId = UUID.randomUUID();
		UUID performanceId = UUID.randomUUID();
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(performanceId, "test", "내용");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(userId, performanceId)).thenReturn(false);
		when(reviewClient.getBookingStatus(userId, performanceId)).thenReturn(new BookingStatusDto("CANCELED"));

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.registerReview(requestDto, userId);
		});

		assertEquals(ResponseCode.BOOKING_NOT_COMPLETED.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 종료되지 않은 공연에 리뷰를 작성하려는 경우")
	void testRegisterReview_fail_earlyReview() {
		// Given
		UUID userId = UUID.randomUUID();
		UUID performanceId = UUID.randomUUID();
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(performanceId, "early", "내용");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(userId, performanceId)).thenReturn(false);
		when(reviewClient.getBookingStatus(userId, performanceId)).thenReturn(new BookingStatusDto("COMPLETED"));
		when(reviewClient.getPerformanceEndTime(performanceId)).thenReturn(
			new PerformanceEndTimeDto(LocalDateTime.now().plusDays(1))); // 종료되지 않음

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.registerReview(requestDto, userId);
		});

		assertEquals(ResponseCode.EARLY_REVIEW.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 유저 정보 없음")
	void testRegisterReview_fail_userNotFound() {
		// Given
		UUID userId = UUID.randomUUID();
		UUID performanceId = UUID.randomUUID();
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(performanceId, "test", "내용");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(userId, performanceId)).thenReturn(false);
		when(reviewClient.getBookingStatus(userId, performanceId)).thenReturn(new BookingStatusDto("COMPLETED"));
		when(reviewClient.getPerformanceEndTime(performanceId)).thenReturn(
			new PerformanceEndTimeDto(LocalDateTime.now().minusDays(1)));
		when(reviewClient.getUserName(userId)).thenThrow(FeignException.NotFound.class);

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.registerReview(requestDto, userId);
		});

		assertEquals(ResponseCode.USER_NOT_FOUND.getMessage(), ex.getMessage());
	}
}
