package com.taken_seat.payment_service.application.dto.service;

import java.util.UUID;

import com.taken_seat.payment_service.domain.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
	// 생성/수정 공통
	private UUID paymentId;        // 수정할 때 필요한 ID (등록 시는 null)

	private UUID bookingId;        // 결제 생성시 필요한 예매 ID

	private Integer amount;         // 결제 금액

	private String orderName;

	private PaymentStatus paymentStatus;   // 결제 상태 (UPDATE 시 필요)

	private UUID userId;           // 결제 생성자(AuthenticatedUser 정보에서)

	private String userEmail;      // 생성자 이메일

}
