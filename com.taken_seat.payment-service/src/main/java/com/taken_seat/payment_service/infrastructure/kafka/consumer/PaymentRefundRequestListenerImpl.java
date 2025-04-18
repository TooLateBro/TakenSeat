package com.taken_seat.payment_service.infrastructure.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.payment_service.application.kafka.consumer.PaymentRefundRequestListener;
import com.taken_seat.payment_service.application.service.PaymentRefundEventHandlerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRefundRequestListenerImpl implements PaymentRefundRequestListener {

	private final PaymentRefundEventHandlerService paymentRefundEventHandlerService;

	@Override
	@KafkaListener(topics = "${kafka.topic.refund-request}", groupId = "${kafka.consumer.group-id}")
	public void handlerRefundRequest(PaymentRefundMessage message) {
		paymentRefundEventHandlerService.processPaymentRefund(message);
	}
}
