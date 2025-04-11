package com.taken_seat.review_service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taken_seat.common_service.dto.AuthenticatedUser;
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

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ReviewClient reviewClient;

	@InjectMocks
	private ReviewService reviewService;

	private UUID testPerformanceId;

	private UUID testPerformanceScheduleId;

	private UUID testReviewId;

	private UUID testAuthorId;

	private String testAuthorEmail;

	private Review testReview;

	private AuthenticatedUser authenticatedUser;

	@BeforeEach
	void setUp() {

		testPerformanceId = UUID.randomUUID();
		testPerformanceScheduleId = UUID.randomUUID();
		testReviewId = UUID.randomUUID();
		testAuthorId = UUID.randomUUID();
		testAuthorEmail = "test@gmail.com";

		testReview = Review.builder()
			.id(testReviewId)
			.performanceId(testPerformanceId)
			.performanceScheduleId(testPerformanceScheduleId)
			.authorId(testAuthorId)
			.authorEmail(testAuthorEmail)
			.title("testReviewTitle")
			.content("testReviewContent")
			.likeCount(0)
			.build();

		testReview.prePersist(UUID.randomUUID());
		reviewRepository.save(testReview);

		authenticatedUser = new AuthenticatedUser(testAuthorId, "홍길동", testAuthorEmail);
	}

	@Test
	@DisplayName("리뷰 등록 - SUCCESS")
	void testRegisterReview_success() {
		// Given
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(testPerformanceId, testPerformanceScheduleId,
			"testRegister", "testContent");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(testAuthorId, testPerformanceId)).thenReturn(false);
		when(reviewClient.getBookingStatus(testAuthorId, testPerformanceId)).thenReturn(
			new BookingStatusDto("COMPLETED"));
		when(reviewClient.getPerformanceEndTime(testPerformanceId, testPerformanceScheduleId)).thenReturn(
			new PerformanceEndTimeDto(LocalDateTime.now().minusDays(1)));
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		ReviewDetailResDto result = reviewService.registerReview(requestDto, authenticatedUser);

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
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(testPerformanceId, testPerformanceScheduleId, "중복",
			"중복내용");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(testAuthorId, testPerformanceId)).thenReturn(true);

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.registerReview(requestDto, authenticatedUser);
		});

		assertEquals(ResponseCode.REVIEW_ALREADY_WRITTEN.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 예매 상태가 완료되지 않음")
	void testRegisterReview_fail_bookingNotCompleted() {
		// Given
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(testPerformanceId, testPerformanceScheduleId, "test",
			"내용");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(testAuthorId, testPerformanceId)).thenReturn(false);
		when(reviewClient.getBookingStatus(testAuthorId, testPerformanceId)).thenReturn(
			new BookingStatusDto("CANCELED"));

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.registerReview(requestDto, authenticatedUser);
		});

		assertEquals(ResponseCode.BOOKING_NOT_COMPLETED.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 종료되지 않은 공연에 리뷰를 작성하려는 경우")
	void testRegisterReview_fail_earlyReview() {
		// Given
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(testPerformanceId, testPerformanceScheduleId,
			"early", "내용");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(testAuthorId, testPerformanceId)).thenReturn(false);
		when(reviewClient.getBookingStatus(testAuthorId, testPerformanceId)).thenReturn(
			new BookingStatusDto("COMPLETED"));
		when(reviewClient.getPerformanceEndTime(testPerformanceId, testPerformanceScheduleId)).thenReturn(
			new PerformanceEndTimeDto(LocalDateTime.now().plusDays(1))); // 종료되지 않음

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.registerReview(requestDto, authenticatedUser);
		});

		assertEquals(ResponseCode.EARLY_REVIEW.getMessage(), ex.getMessage());
	}

}
