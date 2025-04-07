package com.taken_seat.payment_service.domain.repository;

import com.taken_seat.payment_service.domain.model.Payment;

public interface PaymentRepository {

	Payment save(Payment payment);

}
