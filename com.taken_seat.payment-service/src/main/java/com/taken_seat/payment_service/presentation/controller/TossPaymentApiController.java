package com.taken_seat.payment_service.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.payment_service.application.dto.controller.response.PaymentCheckoutResponse;
import com.taken_seat.payment_service.application.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class TossPaymentApiController {

	private final PaymentService paymentService;

	@GetMapping("/checkout-info")
	public ResponseEntity<PaymentCheckoutResponse> getCheckoutInfo(
		@RequestParam UUID bookingId) {

		PaymentCheckoutResponse response = paymentService.getCheckoutInfo(bookingId);

		return ResponseEntity.ok(response);
	}

}
