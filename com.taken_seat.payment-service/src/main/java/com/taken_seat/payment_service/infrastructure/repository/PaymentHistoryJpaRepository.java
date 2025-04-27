package com.taken_seat.payment_service.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;

public interface PaymentHistoryJpaRepository extends JpaRepository<PaymentHistory, UUID> {

	Optional<PaymentHistory> findByPayment(Payment payment);
}
