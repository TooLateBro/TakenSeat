package com.taken_seat.payment_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;

public interface JpaPaymentHistoryRepository extends JpaRepository<PaymentHistory, UUID>, PaymentHistoryRepository {
}
