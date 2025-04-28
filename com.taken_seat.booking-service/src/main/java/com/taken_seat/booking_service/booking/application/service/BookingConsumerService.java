package com.taken_seat.booking_service.booking.application.service;

import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

public interface BookingConsumerService {
	void createPayment(UserBenefitMessage message);

	void updateBooking(PaymentMessage message);

	void updateBooking(PaymentRefundMessage message);

	void updateBenefitUsageHistory(UserBenefitMessage message);

	void acceptFromQueue(BookingRequestMessage message);
}