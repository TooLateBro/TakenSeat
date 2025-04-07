package com.taken_seat.payment_service.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.payment_service.application.dto.request.PaymentCreateReqDto;
import com.taken_seat.payment_service.application.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public ResponseEntity<?> registerPayment(@Valid @RequestBody PaymentCreateReqDto paymentCreateReqDto){

		return ResponseEntity.status(HttpStatus.OK)
			.body(paymentService.registerPayment(paymentCreateReqDto));
	}
}
