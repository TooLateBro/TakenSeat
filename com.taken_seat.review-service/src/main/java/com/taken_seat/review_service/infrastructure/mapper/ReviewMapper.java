package com.taken_seat.review_service.infrastructure.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.review_service.application.dto.controller.request.ReviewRegisterReqDto;
import com.taken_seat.review_service.application.dto.controller.request.ReviewUpdateReqDto;
import com.taken_seat.review_service.application.dto.controller.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.dto.service.ReviewDto;
import com.taken_seat.review_service.domain.model.Review;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

	// Register
	ReviewDto toDto(ReviewRegisterReqDto reqDto, AuthenticatedUser user);

	// Update
	ReviewDto toDto(UUID reviewId, ReviewUpdateReqDto reqDto, AuthenticatedUser user);

	// Delete
	ReviewDto toDto(UUID reviewId, AuthenticatedUser user);

	// 결과 반환용
	ReviewDetailResDto toResponse(Review review);

}
