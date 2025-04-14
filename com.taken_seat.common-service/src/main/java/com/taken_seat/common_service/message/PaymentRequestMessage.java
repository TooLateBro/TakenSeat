package com.taken_seat.common_service.message;

import java.util.UUID;

public class PaymentRequestMessage {

	private UUID bookingId;

	private UUID userId;

	private UUID performanceId;

	private UUID performanceScheduleId;

	private Integer price;
	
}
