package com.taken_seat.payment_service.infrastructure.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.message.PaymentRequestMessage;
import com.taken_seat.payment_service.application.kafka.consumer.PaymentRequestListener;
import com.taken_seat.payment_service.application.service.PaymentEventHandlerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRequestListenerImpl implements PaymentRequestListener {

	private final PaymentEventHandlerService paymentEventHandlerService;

	@Override
	@KafkaListener(topics = "${kafka.topic.payment-request}", groupId = "${kafka.consumer.group-id}")
	public void handlePaymentRequest(PaymentRequestMessage message) {
		paymentEventHandlerService.processPayment(message);
	}
}
