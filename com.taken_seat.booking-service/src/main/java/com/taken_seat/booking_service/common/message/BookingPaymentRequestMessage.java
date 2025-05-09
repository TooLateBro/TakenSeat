package com.taken_seat.booking_service.common.message;

import java.util.UUID;

public record BookingPaymentRequestMessage(
	UUID bookingId,
	Integer amount
) {
}