package com.taken_seat.review_service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.client.ReviewClient;
import com.taken_seat.review_service.application.dto.controller.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.controller.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.dto.service.ReviewDto;
import com.taken_seat.review_service.application.dto.service.ReviewSearchDto;
import com.taken_seat.review_service.application.service.ReviewServiceImpl;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.CustomReviewQuerydslRepository;
import com.taken_seat.review_service.domain.repository.RedisRatingRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;
import com.taken_seat.review_service.infrastructure.client.dto.BookingStatusDto;
import com.taken_seat.review_service.infrastructure.client.dto.PerformanceEndTimeDto;
import com.taken_seat.review_service.infrastructure.mapper.ReviewMapper;

@ExtendWith(MockitoExtension.class)
public class ReviewServicesTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private CustomReviewQuerydslRepository customReviewQuerydslRepository;

	@Mock
	private ReviewClient reviewClient;

	@Mock
	private ReviewMapper reviewMapper;

	@Mock
	private RedisRatingRepository redisRatingRepository;

	@InjectMocks
	private ReviewServiceImpl reviewServices;

	private UUID testPerformanceId;

	private UUID testPerformanceScheduleId;

	private UUID testReviewId;

	private UUID testAuthorId;

	private String testAuthorEmail;

	private Review testReview;

	private ReviewDto testReviewDto;

	private ReviewSearchDto testReviewSearchDto;

	private AuthenticatedUser authenticatedUser;

	@BeforeEach
	void setUp() {

		testPerformanceId = UUID.randomUUID();
		testPerformanceScheduleId = UUID.randomUUID();
		testReviewId = UUID.randomUUID();
		testAuthorId = UUID.randomUUID();
		testAuthorEmail = "test@gmail.com";

		testReviewDto = ReviewDto.builder()
			.performanceId(testPerformanceId)
			.performanceScheduleId(testPerformanceScheduleId)
			.title("testTitle")
			.content("testContent")
			.rating((short)4)
			.userId(UUID.randomUUID())
			.email("test@gmail.com")
			.build();

		testReviewSearchDto = ReviewSearchDto.builder()
			.performance_id(UUID.randomUUID())
			.q("testTitle")
			.category("title")
			.page(0)
			.size(10)
			.sort("createdAt")
			.order("desc")
			.build();

		testReview = Review.builder()
			.id(testReviewId)
			.performanceId(testPerformanceId)
			.performanceScheduleId(testPerformanceScheduleId)
			.authorId(testAuthorId)
			.authorEmail(testAuthorEmail)
			.title("testReviewTitle")
			.content("testReviewContent")
			.likeCount(0)
			.rating((short)1)
			.build();

		testReview.prePersist(UUID.randomUUID());

		authenticatedUser = new AuthenticatedUser(testAuthorId, "test@gmail.com", "MASTER");
	}

	@Test
	@DisplayName("리뷰 등록 - SUCCESS")
	void testRegisterReview_success() {
		// Given
		when(reviewRepository.existsByAuthorIdAndPerformanceIdAndDeletedAtIsNull(testReviewDto.getUserId(),
			testReviewDto.getPerformanceId()))
			.thenReturn(false);

		when(reviewClient.getBookingStatus(testReviewDto.getUserId(),
			testReviewDto.getPerformanceId())).thenReturn(
			new BookingStatusDto("COMPLETED"));

		when(reviewClient.getPerformanceEndTime(testReviewDto.getPerformanceId(),
			testReviewDto.getPerformanceScheduleId())).thenReturn(
			new PerformanceEndTimeDto(LocalDateTime.now().minusDays(1)));

		when(reviewRepository.save(any(Review.class)))
			.thenReturn(testReview);

		ReviewDetailResDto reviewDetailResDto = ReviewDetailResDto.builder()
			.id(testReviewDto.getReviewId())
			.performanceId(testReviewDto.getPerformanceId())
			.performanceScheduleId(testReviewDto.getPerformanceScheduleId())
			.title(testReviewDto.getTitle())
			.content(testReviewDto.getContent())
			.rating(testReviewDto.getRating())
			.build();

		when(reviewMapper.toResponse(any(Review.class)))
			.thenReturn(reviewDetailResDto);

		// When
		ReviewDetailResDto result = reviewServices.registerReview(testReviewDto);

		// Then
		assertNotNull(result);
		assertEquals("testTitle", result.getTitle());
		assertEquals("testContent", result.getContent());
		assertEquals(((short)4), result.getRating());

	}

	@Test
	@DisplayName("리뷰 등록 실패 - 해당 공연에 이미 작성된 리뷰가 존재하는 경우 - FAIL")
	void testRegisterReview_fail_alreadyWritten() {
		// Given
		testReviewDto = ReviewDto.builder()
			.performanceId(testPerformanceId)
			.performanceScheduleId(testPerformanceScheduleId)
			.title("testTitle")
			.content("testContent")
			.rating((short)4)
			.userId(UUID.randomUUID())
			.email("test@gmail.com")
			.build();

		when(reviewRepository.existsByAuthorIdAndPerformanceIdAndDeletedAtIsNull(testReviewDto.getUserId(),
			testReviewDto.getPerformanceId()))
			.thenReturn(true);

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewServices.registerReview(testReviewDto);
		});

		assertEquals(ResponseCode.REVIEW_ALREADY_WRITTEN.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 예매 상태가 완료되지 않음 - FAIL")
	void testRegisterReview_fail_bookingNotCompleted() {
		// Given
		testReviewDto = ReviewDto.builder()
			.performanceId(testPerformanceId)
			.performanceScheduleId(testPerformanceScheduleId)
			.title("testTitle")
			.content("testContent")
			.rating((short)4)
			.userId(UUID.randomUUID())
			.email("test@gmail.com")
			.build();

		when(reviewRepository.existsByAuthorIdAndPerformanceIdAndDeletedAtIsNull(testReviewDto.getUserId(),
			testReviewDto.getPerformanceId()))
			.thenReturn(false);

		when(reviewClient.getBookingStatus(testReviewDto.getUserId(),
			testReviewDto.getPerformanceId())).thenReturn(
			new BookingStatusDto("FAIL"));

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewServices.registerReview(testReviewDto);
		});

		assertEquals(ResponseCode.BOOKING_NOT_COMPLETED.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 종료되지 않은 공연에 리뷰를 작성하려는 경우")
	void testRegisterReview_fail_earlyReview() {
		// Given
		testReviewDto = ReviewDto.builder()
			.performanceId(testPerformanceId)
			.performanceScheduleId(testPerformanceScheduleId)
			.title("testTitle")
			.content("testContent")
			.rating((short)4)
			.userId(UUID.randomUUID())
			.email("test@gmail.com")
			.build();

		when(reviewRepository.existsByAuthorIdAndPerformanceIdAndDeletedAtIsNull(testReviewDto.getUserId(),
			testReviewDto.getPerformanceId()))
			.thenReturn(false);

		when(reviewClient.getBookingStatus(testReviewDto.getUserId(),
			testReviewDto.getPerformanceId())).thenReturn(
			new BookingStatusDto("COMPLETED"));

		when(reviewClient.getPerformanceEndTime(testReviewDto.getPerformanceId(),
			testReviewDto.getPerformanceScheduleId())).thenReturn(
			new PerformanceEndTimeDto(LocalDateTime.now().plusDays(1)));

		// When & Then
		ReviewException ex = assertThrows(ReviewException.class, () -> {
			reviewServices.registerReview(testReviewDto);
		});

		assertEquals(ResponseCode.EARLY_REVIEW.getMessage(), ex.getMessage());
	}

	@Test
	@DisplayName("리뷰 단건 조회 - SUCCESS")
	void testGetReviewDetail_success() {
		// Given
		when(reviewRepository.findByIdAndDeletedAtIsNull(testReviewId)).thenReturn(Optional.of(testReview));

		ReviewDetailResDto reviewDetailResDto = ReviewDetailResDto.builder()
			.id(testReview.getId())
			.performanceId(testReview.getPerformanceId())
			.performanceScheduleId(testReview.getPerformanceScheduleId())
			.title(testReview.getTitle())
			.content(testReview.getContent())
			.rating(testReview.getRating())
			.authorEmail(testReview.getAuthorEmail())
			.build();

		when(reviewMapper.toResponse(any(Review.class)))
			.thenReturn(reviewDetailResDto);

		// When
		ReviewDetailResDto result = reviewServices.getReviewDetail(testReviewId);

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
			reviewServices.getReviewDetail(TestRegisterReviewId);
		});

		assertEquals(ResponseCode.REVIEW_NOT_FOUND.getMessage(), ex.getMessage());

	}

	@Test
	@DisplayName("리뷰 리스트 검색 - 제목(title) 필터 적용 - SUCCESS")
	void testSearchReview_success_withTitleFilter() {

		// Given
		Page<Review> mockPage = new PageImpl<>(Collections.singletonList(testReview),
			PageRequest.of(testReviewSearchDto.getPage(), testReviewSearchDto.getSize()), 1);

		when(customReviewQuerydslRepository.search(testReviewSearchDto)).thenReturn(
			mockPage);

		// When
		PageReviewResponseDto result = reviewServices.searchReview(testReviewSearchDto);

		// Then
		assertNotNull(result); // result가 null이 아님을 확인
		assertNotNull(result.getContent()); // content 리스트가 null이 아님을 확인
		assertEquals(1, result.getTotalElements());
		assertEquals(1, result.getContent().size());
	}

	@Test
	@DisplayName("리뷰 리스트 검색 - 비어있는 결과 - SUCCESS")
	void testSearchReview_success_emptyResult() {
		// Given
		when(customReviewQuerydslRepository.search(any()))
			.thenReturn(Page.empty());

		// When
		PageReviewResponseDto result = reviewServices.searchReview(testReviewSearchDto);

		// Then
		assertNotNull(result);
		assertEquals(0, result.getTotalElements());
		assertEquals(0, result.getContent().size());
		assertTrue(result.getContent().isEmpty());
	}

	@Test
	@DisplayName("리뷰 수정 - SUCCESS")
	void testUpdateReview_success() {
		// Given
		testReviewDto = ReviewDto.builder()
			.reviewId(testReviewId)
			.performanceId(testPerformanceId)
			.performanceScheduleId(testPerformanceScheduleId)
			.title("updateTitle")
			.content("updateContent")
			.rating((short)4)
			.userId(UUID.randomUUID())
			.email("test@gmail.com")
			.role("ADMIN")
			.build();

		ReviewDetailResDto reviewDetailResDto = ReviewDetailResDto.builder()
			.id(testReview.getId())
			.performanceId(testReview.getPerformanceId())
			.performanceScheduleId(testReview.getPerformanceScheduleId())
			.title("updateTitle")
			.content("updateContent")
			.rating(testReview.getRating())
			.authorEmail(testReview.getAuthorEmail())
			.build();

		when(reviewRepository.findByIdAndDeletedAtIsNull(testReviewId)).thenReturn(Optional.of(testReview));

		when(reviewMapper.toResponse(any(Review.class))).thenReturn(reviewDetailResDto);
		// When
		ReviewDetailResDto result = reviewServices.updateReview(testReviewDto);

		// Then
		assertNotNull(result);
		assertEquals("updateTitle", result.getTitle());
		assertEquals("updateContent", result.getContent());

	}

	@Test
	@DisplayName("리뷰 수정 실패 - 존재하지 않는 결제 ID - FAIL")
	void testUpdateReview__fail_reviewNotFound() {
		// Given
		UUID notExistId = UUID.randomUUID();
		testReviewDto = ReviewDto.builder()
			.reviewId(notExistId)
			.performanceId(testPerformanceId)
			.performanceScheduleId(testPerformanceScheduleId)
			.title("updateTitle")
			.content("updateContent")
			.rating((short)4)
			.userId(UUID.randomUUID())
			.email("test@gmail.com")
			.role("ADMIN")
			.build();

		when(reviewRepository.findByIdAndDeletedAtIsNull(notExistId)).thenReturn(Optional.empty());

		// When & Then
		ReviewException exception = assertThrows(ReviewException.class, () -> {
			reviewServices.updateReview(testReviewDto);
		});

		assertEquals("해당 리뷰가 존재하지않습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("리뷰 수정 실패 - 작성자도 아니고 마스터도 아님 - FAIL")
	void testUpdateReview_fail_forbiddenAccess() {
		// Given
		testReviewDto = ReviewDto.builder()
			.reviewId(testReviewId)
			.performanceId(testPerformanceId)
			.performanceScheduleId(testPerformanceScheduleId)
			.title("updateTitle")
			.content("updateContent")
			.rating((short)4)
			.userId(UUID.randomUUID())
			.email("test@gmail.com")
			.role("CONSUMER")
			.build();

		when(reviewRepository.findByIdAndDeletedAtIsNull(testReviewId)).thenReturn(Optional.of(testReview));

		// When & Then
		ReviewException exception = assertThrows(ReviewException.class, () -> {
			reviewServices.updateReview(testReviewDto);
		});

		assertEquals("해당 리뷰에 접근할 권한이 없습니다.", exception.getMessage()); // FORBIDDEN_REVIEW_ACCESS 메시지
	}

	@Test
	@DisplayName("리뷰 논리적 삭제 성공 - SUCCESS")
	void testDeleteReview_success() {
		// Given
		testReviewDto = ReviewDto.builder()
			.reviewId(testReviewId)
			.userId(UUID.randomUUID())
			.email("test@gmail.com")
			.role("ADMIN")
			.build();

		when(reviewRepository.findByIdAndDeletedAtIsNull(testReviewDto.getReviewId())).thenReturn(
			Optional.of(testReview));

		// When
		assertDoesNotThrow(() -> reviewServices.deleteReview(testReviewDto));

		// Then
		assertNotNull(testReview.getDeletedAt());
	}

	@Test
	@DisplayName("리뷰 논리적 삭제 실패 - 존재하지않는 결제 ID - FAIL")
	void testDeletePayment_fail_paymentNotFound() {
		// Given
		testReviewDto = ReviewDto.builder()
			.reviewId(UUID.randomUUID())
			.userId(authenticatedUser.getUserId())
			.email("test@gmail.com")
			.role("ADMIN")
			.build();

		when(reviewRepository.findByIdAndDeletedAtIsNull(testReviewDto.getReviewId())).thenReturn(Optional.empty());

		UUID notAuthorId = UUID.randomUUID();
		AuthenticatedUser anotherUser = new AuthenticatedUser(notAuthorId, "other@gmail.com", "other@gmail.com");
		// When & Then
		ReviewException exception = assertThrows(ReviewException.class, () ->
			reviewServices.deleteReview(testReviewDto)
		);

		assertEquals("해당 리뷰가 존재하지않습니다.", exception.getMessage());

	}

	@Test
	@DisplayName("리뷰 수정 실패 - 작성자도 아니고 마스터도 아님 - FAIL")
	void testDeleteReview_fail_forbiddenAccess() {
		// Given

		testReviewDto = ReviewDto.builder()
			.reviewId(testReviewId)
			.userId(UUID.randomUUID())
			.email("test@gmail.com")
			.role("CONSUMER")
			.build();

		when(reviewRepository.findByIdAndDeletedAtIsNull(testReviewDto.getReviewId())).thenReturn(
			Optional.of(testReview));

		// When & Then
		ReviewException exception = assertThrows(ReviewException.class, () -> {
			reviewServices.deleteReview(testReviewDto);
		});

		assertEquals("해당 리뷰에 접근할 권한이 없습니다.", exception.getMessage()); // FORBIDDEN_REVIEW_ACCESS 메시지
	}
}
