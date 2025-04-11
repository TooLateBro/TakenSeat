package com.taken_seat.booking_service.ticket.application.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.taken_seat.booking_service.ticket.domain.Ticket;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketPageResponse {
	private List<TicketReadResponse> content;
	private int pageNumber;
	private int pageSize;
	private int totalPages;
	private long totalElements;
	private boolean isLast;

	public static TicketPageResponse toDto(Page<Ticket> page) {
		return TicketPageResponse.builder()
			.content(page.getContent().stream()
				.map(TicketReadResponse::toDto)
				.collect(Collectors.toList()))
			.pageNumber(page.getNumber())
			.pageSize(page.getSize())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.isLast(page.isLast())
			.build();
	}
}