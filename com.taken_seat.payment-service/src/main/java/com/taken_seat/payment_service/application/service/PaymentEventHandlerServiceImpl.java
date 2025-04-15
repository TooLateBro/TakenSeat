package com.taken_seat.payment_service.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.message.PaymentRequestMessage;
import com.taken_seat.common_service.message.PaymentResultMessage;
import com.taken_seat.common_service.message.enums.PaymentResultStatus;
import com.taken_seat.payment_service.application.kafka.producer.PaymentResultProducer;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentEventHandlerServiceImpl implements PaymentEventHandlerService {

	private final PaymentRepository paymentRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	
	private final PaymentResultProducer paymentResultProducer;

	@Override
	public void processPayment(PaymentRequestMessage message) {

		if (isInvalidPrice(message)) {
			PaymentResultMessage paymentResultMessage = PaymentResultMessage.builder()
				.bookingId(message.getBookingId())
				.status(PaymentResultStatus.INVALID_PRICE)
				.build();

			paymentResultProducer.sendPaymentResult(paymentResultMessage);
			return;
		}

		// 2. 결제 엔티티 기본 생성 및 저장
		Payment payment = Payment.register(message);
		paymentRepository.save(payment);

		// 결제 히스토리 생성 및 저장
		PaymentHistory paymentHistory = PaymentHistory.register(payment);
		paymentHistoryRepository.save(paymentHistory);

		payment.markAsCompleted(message.getUserId());
		paymentHistory.markAsCompleted(message.getUserId());

		PaymentResultMessage paymentResultMessage = PaymentResultMessage.builder()
			.bookingId(message.getBookingId())
			.paymentId(payment.getId())
			.status(PaymentResultStatus.SUCCESS)
			.build();

		paymentResultProducer.sendPaymentResult(paymentResultMessage);
	}

	private boolean isInvalidPrice(PaymentRequestMessage message) {
		return message.getPrice() == null || message.getPrice() <= 0;
	}

}





