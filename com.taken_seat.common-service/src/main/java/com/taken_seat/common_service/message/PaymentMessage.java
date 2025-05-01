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
public class PaymentMessage {

	private UUID bookingId; // 예약 ID

	private UUID userId; // 사용자 ID (결제 요청 시에만 필요)

	private UUID paymentId; // 결제 ID (결제 결과 메시지에서 필요)

	private Integer amount; // 결제 금액

	private String orderName;

	private PaymentResultStatus status; // 결제 상태 (결제 요청과 결과에서 모두 사용)

	private MessageType type; // 메시지 타입 (REQUEST / RESULT)

	// 결제 요청과 결과 메시지를 구분할 수 있는 Enum
	public enum MessageType {
		REQUEST, // 결제 요청 메시지
		RESULT // 결제 결과 메시지
	}

	public enum PaymentResultStatus {
		SUCCESS,                // 결제 성공
		FAIL,
		INVALID_PRICE,          // 잘못된 가격
	}
}
