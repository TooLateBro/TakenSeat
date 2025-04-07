package com.taken_seat.payment_service.domain.repository;

import com.taken_seat.payment_service.domain.model.PaymentHistory;

public interface PaymentHistoryRepository {

	PaymentHistory save (PaymentHistory paymentHistory);
}
