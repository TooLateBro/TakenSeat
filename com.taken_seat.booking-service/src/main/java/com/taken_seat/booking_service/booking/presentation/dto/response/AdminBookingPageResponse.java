package com.taken_seat.booking_service.booking.presentation.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.taken_seat.booking_service.booking.domain.BookingQuery;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminBookingPageResponse {
	private List<AdminBookingReadResponse> content;
	private int pageNumber;
	private int pageSize;
	private int totalPages;
	private long totalElements;
	private boolean isLast;

	public static AdminBookingPageResponse toDto(Page<BookingQuery> page) {
		return AdminBookingPageResponse.builder()
			.content(page.getContent().stream()
				.map(AdminBookingReadResponse::toDto)
				.collect(Collectors.toList()))
			.pageNumber(page.getNumber())
			.pageSize(page.getSize())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.isLast(page.isLast())
			.build();
	}
}