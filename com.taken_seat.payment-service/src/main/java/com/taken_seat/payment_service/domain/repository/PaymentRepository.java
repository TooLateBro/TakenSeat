package com.taken_seat.payment_service.domain.repository;

import org.springframework.stereotype.Repository;

import com.taken_seat.payment_service.domain.model.Payment;

@Repository
public interface PaymentRepository {

	Payment save(Payment payment);

}