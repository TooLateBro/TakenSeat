package com.taken_seat.booking_service.booking.application.service;

import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

public interface BookingProducer {
	void sendPaymentRequest(PaymentMessage message);

	void sendTicketRequest(TicketRequestMessage message);

	void sendBenefitUsageRequest(UserBenefitMessage message);

	void sendBenefitRefundRequest(UserBenefitMessage message);

	void sendBenefitPaymentResult(UserBenefitMessage message);
}