package com.taken_seat.payment_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;

public interface JpaPaymentRepository extends JpaRepository<Payment, UUID>, PaymentRepository{
}
