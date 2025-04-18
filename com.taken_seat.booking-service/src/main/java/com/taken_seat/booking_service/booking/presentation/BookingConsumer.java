package com.taken_seat.booking_service.booking.presentation;

import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

public interface BookingConsumer {
	void updateBooking(PaymentMessage message);

	void updateBooking(PaymentRefundMessage message);

	void createPayment(UserBenefitMessage message);

	void updateBenefitUsageHistory(UserBenefitMessage message);
}