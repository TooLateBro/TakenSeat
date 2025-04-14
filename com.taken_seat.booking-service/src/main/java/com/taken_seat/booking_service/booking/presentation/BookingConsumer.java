package com.taken_seat.booking_service.booking.presentation;

import com.taken_seat.common_service.message.PaymentResultMessage;

public interface BookingConsumer {
	void updateBooking(PaymentResultMessage message);
}