package com.taken_seat.payment_service.application.dto.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSearchDto {
	private String q;
	private String category;
	private Integer page;
	private Integer size;
	private String sort;
	private String order;
}
