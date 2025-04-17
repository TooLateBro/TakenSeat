package com.taken_seat.review_service.application.dto.response;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PageReviewResponseDto implements Serializable {
	private List<ReviewDetailResDto> content;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private int currentPage;
	private double avgRating;

	public static PageReviewResponseDto toResponse(Page<ReviewDetailResDto> page, double avgRating) {
		return PageReviewResponseDto.builder()
			.content(page.getContent())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.pageSize(page.getSize())
			.currentPage(page.getNumber() + 1)
			.avgRating(avgRating)
			.build();
	}
}
