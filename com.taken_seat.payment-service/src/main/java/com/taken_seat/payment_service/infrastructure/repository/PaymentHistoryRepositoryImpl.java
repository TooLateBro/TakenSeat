package com.taken_seat.payment_service.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentHistoryRepositoryImpl implements PaymentHistoryRepository {

	private final PaymentHistoryJpaRepository paymentHistoryRepository;

	@Override
	public PaymentHistory save(PaymentHistory paymentHistory) {
		return paymentHistoryRepository.save(paymentHistory);
	}

	@Override
	public Optional<PaymentHistory> findByPayment(Payment payment) {
		return paymentHistoryRepository.findByPayment(payment);
	}
}
