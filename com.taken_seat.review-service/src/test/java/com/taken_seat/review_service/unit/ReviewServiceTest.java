package com.taken_seat.review_service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
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

	private UUID testReviewId;

	private UUID testAuthorId;

	private String testAuthorEmail;

	private Review testReview;

	private AuthenticatedUser authenticatedUser;

	@BeforeEach
	void setUp() {

		testPerformanceId = UUID.randomUUID();
		testReviewId = UUID.randomUUID();
		testAuthorId = UUID.randomUUID();
		testAuthorEmail = "test@gmail.com";

		testReview = Review.builder()
			.id(testReviewId)
			.performanceId(testPerformanceId)
			.authorId(testAuthorId)
			.authorEmail(testAuthorEmail)
			.title("testReviewTitle")
			.content("testReviewContent")
			.likeCount(0)
			.build();

		testReview.prePersist(UUID.randomUUID());
		reviewRepository.save(testReview);

		authenticatedUser = new AuthenticatedUser(testAuthorId, "test@gmail.com", testAuthorEmail);
	}

	@Test
	@DisplayName("리뷰 등록 - SUCCESS")
	void testRegisterReview_success() {
		// Given
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(testPerformanceId, "testRegister", "testContent");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(testAuthorId, testPerformanceId)).thenReturn(false);
		when(reviewClient.getBookingStatus(testAuthorId, testPerformanceId)).thenReturn(
			new BookingStatusDto("COMPLETED"));
		when(reviewClient.getPerformanceEndTime(testPerformanceId)).thenReturn(
			new PerformanceEndTimeDto(LocalDateTime.now().minusDays(1)));
		when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		ReviewDetailResDto result = reviewService.registerReview(requestDto, authenticatedUser);

		// Then
		assertNotNull(result);
		assertEquals("testRegister", result.getTitle());
		assertEquals("testContent", result.getContent());
		assertEquals("test@gmail.com", result.getAuthorEmail());

	}

	@Test
	@DisplayName("리뷰 등록 실패 - 해당 공연에 이미 작성된 리뷰가 존재하는 경우 - FAIL")
	void testRegisterReview_fail_alreadyWritten() {
		// Given
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(testPerformanceId, "중복", "중복내용");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(testAuthorId, testPerformanceId)).thenReturn(true);

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.registerReview(requestDto, authenticatedUser);
		});

		assertEquals(ResponseCode.REVIEW_ALREADY_WRITTEN.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 예매 상태가 완료되지 않음 - FAIL")
	void testRegisterReview_fail_bookingNotCompleted() {
		// Given
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(testPerformanceId, "test", "내용");

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
		ReviewRegisterReqDto requestDto = new ReviewRegisterReqDto(testPerformanceId, "early", "내용");

		when(reviewRepository.existsByAuthorIdAndPerformanceId(testAuthorId, testPerformanceId)).thenReturn(false);
		when(reviewClient.getBookingStatus(testAuthorId, testPerformanceId)).thenReturn(
			new BookingStatusDto("COMPLETED"));
		when(reviewClient.getPerformanceEndTime(testPerformanceId)).thenReturn(
			new PerformanceEndTimeDto(LocalDateTime.now().plusDays(1))); // 종료되지 않음

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.registerReview(requestDto, authenticatedUser);
		});

		assertEquals(ResponseCode.EARLY_REVIEW.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 단건 조회 - SUCCESS")
	void testGetReviewDetail_success() {
		// Given
		when(reviewRepository.findByIdAndDeletedAtIsNull(testReviewId)).thenReturn(Optional.of(testReview));

		// When
		ReviewDetailResDto result = reviewService.getReviewDetail(testReviewId);

		// Then
		assertNotNull(result);
		assertEquals(testReview.getTitle(), result.getTitle());
		assertEquals(testReview.getContent(), result.getContent());
		assertEquals(testReview.getAuthorEmail(), result.getAuthorEmail());
	}

	@Test
	@DisplayName("리뷰 단건 조회 실패 - 존재하지않는 UUID로 조회 시도 - FAIL ")
	void testGetReviewDetail_fail_reviewNotFound() {
		// Given
		UUID TestRegisterReviewId = UUID.randomUUID();
		when(reviewRepository.findByIdAndDeletedAtIsNull(TestRegisterReviewId)).thenReturn(Optional.empty());

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewService.getReviewDetail(TestRegisterReviewId);
		});

		assertEquals(ResponseCode.REVIEW_NOT_FOUND.getMessage(), ex.getMessage());

	}
}
