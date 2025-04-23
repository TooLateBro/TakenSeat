package com.taken_seat.booking_service.booking.application.dto.response;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;

import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;

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

	public static BookingPageResponse toDto(Page<Booking> page, List<TicketPerformanceClientResponse> responses) {
		List<BookingReadResponse> content = IntStream.range(0, page.getContent().size())
			.mapToObj(i -> BookingReadResponse.toDto(page.getContent().get(i), responses.get(i)))
			.toList();

		return BookingPageResponse.builder()
			.content(content)
			.pageNumber(page.getNumber())
			.pageSize(page.getSize())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.isLast(page.isLast())
			.build();
	}
}