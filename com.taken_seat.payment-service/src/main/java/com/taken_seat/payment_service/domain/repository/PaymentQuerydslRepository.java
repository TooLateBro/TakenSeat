package com.taken_seat.payment_service.domain.repository;

import org.springframework.data.domain.Page;

import com.taken_seat.payment_service.domain.model.Payment;

public interface PaymentQuerydslRepository {
	Page<Payment> findAll(String q, String category, int page, int size, String sort, String order);

}
