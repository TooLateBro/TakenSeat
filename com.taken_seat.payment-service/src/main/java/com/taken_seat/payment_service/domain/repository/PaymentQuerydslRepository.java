package com.taken_seat.payment_service.domain.repository;

import org.springframework.data.domain.Page;

import com.taken_seat.payment_service.application.dto.service.PaymentSearchDto;
import com.taken_seat.payment_service.domain.model.Payment;

public interface PaymentQuerydslRepository {
	Page<Payment> search(PaymentSearchDto searchReqDto);

}
