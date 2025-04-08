package com.taken_seat.payment_service.application.dto.response;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PagePaymentResponseDto implements Serializable {
	private List<PaymentDetailResDto> content;
	private int totalPages;
	private long totalElements;
	private int pageSize;
	private int currentPage;

	public static PagePaymentResponseDto toResponse(Page<PaymentDetailResDto> page) {
		return PagePaymentResponseDto.builder()
			.content(page.getContent())
			.totalPages(page.getTotalPages())
			.totalElements(page.getTotalElements())
			.pageSize(page.getSize())
			.currentPage(page.getNumber() + 1)
			.build();
	}
}
