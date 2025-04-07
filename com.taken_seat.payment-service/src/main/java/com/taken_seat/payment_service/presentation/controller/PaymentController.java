package com.taken_seat.payment_service.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

	private PaymentService paymentService;

	@PostMapping
	public ResponseEntity<?> registerPayment(@Valid PaymentCreateReqDto paymentCreateReqDto){

		return ResponseEntity.status(HttpStatus.OK)
			.body(paymentService.registerPayment(paymentCreateReqDto));
	}
}
