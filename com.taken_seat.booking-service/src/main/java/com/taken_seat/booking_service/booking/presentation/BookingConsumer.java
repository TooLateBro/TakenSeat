package com.taken_seat.booking_service.booking.presentation;

import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

public interface BookingConsumer {
	void updateBooking(PaymentMessage message);

	void createPayment(UserBenefitMessage message);
}