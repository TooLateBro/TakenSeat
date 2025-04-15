package com.taken_seat.payment_service.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.customException.PaymentHistoryException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.PaymentResultMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;
import com.taken_seat.common_service.message.enums.PaymentResultStatus;
import com.taken_seat.payment_service.application.kafka.producer.PaymentResultProducer;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserBenefitEventHandlerServiceImpl implements UserBenefitEventHandlerService {

	private final PaymentRepository paymentRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final PaymentResultProducer paymentResultProducer;

	@Override
	public void handleUserBenefitUsed(UserBenefitMessage message) {

		Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(message.getPaymentId())
			.orElseThrow(() -> new PaymentException(ResponseCode.PAYMENT_NOT_FOUND_EXCEPTION));

		PaymentHistory paymentHistory = paymentHistoryRepository.findByPayment(payment)
			.orElseThrow(() -> new PaymentHistoryException(ResponseCode.PAYMENT_HISTORY_NOT_FOUND_EXCEPTION));

		int price = payment.getPrice();

		if (!message.getStatus().equals(UserBenefitMessage.UserBenefitStatus.SUCCESS)) {

			payment.updateStatus(PaymentStatus.FAILED);
			paymentHistory.updateHistory(payment);

			PaymentResultMessage paymentResultMessage = PaymentResultMessage.builder()
				.bookingId(payment.getBookingId())
				.status(PaymentResultStatus.COUPON_OR_MILEAGE_FAIL)
				.build();

			paymentResultProducer.sendPaymentResult(paymentResultMessage);
			return;
		}

		if (message.getDiscount() != null) {
			double discountAmount = price * (message.getDiscount() / 100.0);  // 할인 금액 계산
			price = (int)(price - discountAmount); // 할인된 가격 계산

			if (!validPrice(price)) {

				payment.updateStatus(PaymentStatus.FAILED);
				paymentHistory.updateHistory(payment);
				throw new PaymentException(ResponseCode.INVALID_COUPON);
			}
		}

		if (message.getMileage() != null) {
			price -= message.getMileage();

			if (!validPrice(price)) {
				payment.updateStatus(PaymentStatus.FAILED);
				paymentHistory.updateHistory(payment);
				throw new PaymentException(ResponseCode.INVALID_MILEAGE);
			}
		}

		payment.updatePrice(price);
		payment.markAsCompleted(payment.getUserId());

		paymentHistory.markAsCompleted(payment.getUserId());
		paymentHistory.updateHistory(payment);

		PaymentResultMessage paymentResultMessage = PaymentResultMessage.builder()
			.bookingId(payment.getBookingId())
			.status(PaymentResultStatus.SUCCESS)
			.build();

		paymentResultProducer.sendPaymentResult(paymentResultMessage);
	}

	private boolean validPrice(int price) {
		return price > 0;
	}
}
