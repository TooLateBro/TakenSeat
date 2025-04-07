package com.taken_seat.payment_service.domain.repository;

import org.springframework.stereotype.Repository;

import com.taken_seat.payment_service.domain.model.PaymentHistory;

@Repository
public interface PaymentHistoryRepository {

	PaymentHistory save (PaymentHistory paymentHistory);
}
