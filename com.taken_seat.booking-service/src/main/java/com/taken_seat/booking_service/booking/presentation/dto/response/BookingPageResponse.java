package com.taken_seat.booking_service.booking.presentation.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.taken_seat.booking_service.booking.domain.BookingQuery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class BookingPageResponse {
	private List<BookingReadResponse> content;
	private int pageNumber;
	private int pageSize;
	private int totalPages;
	private long totalElements;
	private Boolean isLast;

	public static BookingPageResponse toDto(Page<BookingQuery> page) {

		return BookingPageResponse.builder()
			.content(page.getContent().stream()
				.map(BookingReadResponse::toDto)
				.toList()
			)
			.pageNumber(page.getNumber())
			.pageSize(page.getSize())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.isLast(page.isLast())
			.build();
	}
}