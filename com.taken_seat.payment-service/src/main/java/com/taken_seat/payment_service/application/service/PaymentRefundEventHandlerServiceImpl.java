package com.taken_seat.payment_service.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.customException.PaymentHistoryException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.payment_service.application.kafka.producer.PaymentRefundResultProducer;
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
public class PaymentRefundEventHandlerServiceImpl implements PaymentRefundEventHandlerService {

	private final PaymentRepository paymentRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;

	private final PaymentRefundResultProducer paymentRefundResultProducer;

	@Override
	public void processPaymentRefund(PaymentRefundMessage message) {

		// 1. 결제 금액 검사
		if (isInvalidPrice(message)) {
			log.warn("[Payment] 잘못된 환불 금액 - bookingId: {}, price: {}", message.getBookingId(),
				message.getPrice());
			PaymentRefundMessage paymentRefundMessage = PaymentRefundMessage.builder()
				.userId(message.getUserId())
				.bookingId(message.getBookingId())
				.status(PaymentRefundMessage.PaymentRefundStatus.INVALID_PRICE)
				.type(PaymentRefundMessage.MessageType.RESULT)
				.build();

			paymentRefundResultProducer.sendPaymentRefundResult(paymentRefundMessage);

			log.info("[Payment] 환불 실패 메시지 전송 완료 - bookingId: {}", message.getBookingId());
			return;
		}

		Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(message.getPaymentId())
			.orElseThrow(() -> new PaymentException(ResponseCode.PAYMENT_NOT_FOUND_EXCEPTION));

		PaymentHistory paymentHistory = paymentHistoryRepository.findByPayment(payment)
			.orElseThrow(() -> new PaymentHistoryException(ResponseCode.PAYMENT_HISTORY_NOT_FOUND_EXCEPTION));

		payment.refund(message);
		log.debug("[Payment] 환불 처리 완료 - paymentId: {} , price: {}", payment.getId(), payment.getPrice());
		paymentHistory.refund(payment);
		log.debug("[Payment] 환불 히스토리 저장 완료 - paymentHistoryId: {}", paymentHistory.getId());

		PaymentRefundMessage paymentRefundMessage = PaymentRefundMessage.builder()
			.userId(message.getUserId())
			.bookingId(message.getBookingId())
			.status(PaymentRefundMessage.PaymentRefundStatus.SUCCESS)
			.type(PaymentRefundMessage.MessageType.RESULT)
			.build();

		paymentRefundResultProducer.sendPaymentRefundResult(paymentRefundMessage);
		log.info("[Payment] 환불 성공 메시지 전송 완료 - bookingId: {}", message.getBookingId());
	}

	private boolean isInvalidPrice(PaymentRefundMessage message) {
		return message.getPrice() == null || message.getPrice() <= 0;
	}
}
