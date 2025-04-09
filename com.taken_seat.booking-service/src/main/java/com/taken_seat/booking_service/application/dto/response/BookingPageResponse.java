package com.taken_seat.booking_service.application.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.taken_seat.booking_service.domain.Booking;

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
	private boolean isLast;

	public static BookingPageResponse toDto(Page<Booking> page) {
		return BookingPageResponse.builder()
			.content(page.getContent().stream()
				.map(BookingReadResponse::toDto)
				.collect(Collectors.toList()))
			.pageNumber(page.getNumber())
			.pageSize(page.getSize())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.isLast(page.isLast())
			.build();
	}
}