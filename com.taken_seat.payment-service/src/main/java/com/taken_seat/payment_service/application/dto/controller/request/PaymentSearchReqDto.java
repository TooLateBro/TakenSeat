package com.taken_seat.payment_service.application.dto.controller.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentSearchReqDto {
	private String q;
	private String category;
	private int page = 0;
	private int size = 10;
	private String sort = "createdAt";
	private String order = "desc";
}
