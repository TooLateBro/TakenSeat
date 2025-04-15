package com.taken_seat.payment_service.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.message.PaymentMessage;
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
	public void processPayment(PaymentMessage message) {

		// 1. 결제 금액 검사
		if (isInvalidPrice(message)) {
			PaymentMessage paymentResultMessage = PaymentMessage.builder()
				.bookingId(message.getBookingId())
				.status(PaymentMessage.PaymentResultStatus.INVALID_PRICE)
				.build();

			paymentResultProducer.sendPaymentResult(paymentResultMessage);
			return;
		}

		// 2. 결제 엔티티 기본 생성 및 저장
		Payment payment = Payment.register(message);
		paymentRepository.save(payment);

		// 3. 결제 히스토리 생성 및 저장
		PaymentHistory paymentHistory = PaymentHistory.register(payment);
		paymentHistoryRepository.save(paymentHistory);

		payment.markAsCompleted(message.getUserId());
		paymentHistory.markAsCompleted(message.getUserId());

		// 4. 성공 메시지 전달
		PaymentMessage paymentResultMessage = PaymentMessage.builder()
			.bookingId(message.getBookingId())
			.paymentId(payment.getId())
			.status(PaymentMessage.PaymentResultStatus.SUCCESS)
			.type(PaymentMessage.MessageType.RESULT)
			.build();

		paymentResultProducer.sendPaymentResult(paymentResultMessage);
	}

	private boolean isInvalidPrice(PaymentMessage message) {
		return message.getPrice() == null || message.getPrice() <= 0;
	}

}





