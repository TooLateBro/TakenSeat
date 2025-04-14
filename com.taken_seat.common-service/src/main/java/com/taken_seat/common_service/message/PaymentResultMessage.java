package com.taken_seat.common_service.message;

import java.util.UUID;

import com.taken_seat.common_service.message.enums.PaymentResultStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResultMessage {

	private UUID bookingId;

	private UUID paymentId;

	private PaymentResultStatus status;

}
