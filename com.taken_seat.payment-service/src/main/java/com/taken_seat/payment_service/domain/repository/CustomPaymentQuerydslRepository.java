package com.taken_seat.payment_service.domain.repository;

import org.springframework.data.domain.Page;

import com.taken_seat.payment_service.application.dto.service.PaymentSearchDto;
import com.taken_seat.payment_service.domain.model.Payment;

public interface CustomPaymentQuerydslRepository {
	Page<Payment> search(PaymentSearchDto searchReqDto);

}
