package com.taken_seat.common_service.message;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRefundMessage {

	private UUID bookingId;

	private UUID paymentId;

	private UUID userId;

	private Integer amount;

	private String cancelReason;

	private PaymentRefundStatus status; // 결제 상태 (결제 요청과 결과에서 모두 사용)

	private MessageType type;

	public enum MessageType {
		REQUEST, // 결제 요청 메시지
		RESULT // 결제 결과 메시지
	}

	public enum PaymentRefundStatus {
		SUCCESS,
		FAIL,
		INVALID_PRICE
	}
}
