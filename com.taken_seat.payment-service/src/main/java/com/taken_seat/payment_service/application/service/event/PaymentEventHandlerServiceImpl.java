package com.taken_seat.payment_service.application.service.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.payment_service.application.kafka.producer.PaymentResponseProducer;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentEventHandlerServiceImpl implements PaymentEventHandlerService {

	private final PaymentRepository paymentRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;

	private final PaymentResponseProducer paymentResponseProducer;

	@Override
	public void processPayment(PaymentMessage message) {

		// 1. 결제 금액 검사
		if (isInvalidPrice(message)) {
			log.warn("[Payment] 결제 금액 유효성 검사 - 실패 - bookingId={}, price={}", message.getBookingId(),
				message.getAmount());

			PaymentMessage paymentResultMessage = PaymentMessage.builder()
				.userId(message.getUserId())
				.bookingId(message.getBookingId())
				.status(PaymentMessage.PaymentResultStatus.INVALID_PRICE)
				.type(PaymentMessage.MessageType.RESULT)
				.build();

			paymentResponseProducer.sendPaymentResponse(paymentResultMessage);

			log.info("[Payment] 결제 생성 실패 메시지 전송 - 성공 - bookingId={}", message.getBookingId());
			return;
		}

		// 2. 결제 엔티티 기본 생성 및 저장
		Payment payment = Payment.register(message);
		paymentRepository.save(payment);
		log.debug("[Payment] 결제 생성 - 성공 - paymentId={}, price={}", payment.getId(), payment.getAmount());

		// 3. 결제 히스토리 생성 및 저장
		PaymentHistory paymentHistory = PaymentHistory.register(payment);
		paymentHistoryRepository.save(paymentHistory);
		log.debug("[Payment] 결제 히스토리 생성 - 성공 - paymentHistoryId={}", paymentHistory.getId());

		// 4. 성공 메시지 전달
		PaymentMessage paymentResultMessage = PaymentMessage.builder()
			.userId(message.getUserId())
			.bookingId(message.getBookingId())
			.paymentId(payment.getId())
			.status(PaymentMessage.PaymentResultStatus.SUCCESS)
			.type(PaymentMessage.MessageType.RESULT)
			.build();

		paymentResponseProducer.sendPaymentResponse(paymentResultMessage);
		log.info("[Payment] 결제 생성 성공 메시지 전송 - 성공 - bookingId={}, paymentId={}", message.getBookingId(),
			payment.getId());
	}

	private boolean isInvalidPrice(PaymentMessage message) {
		return message.getAmount() == null || message.getAmount() <= 0;
	}

}





