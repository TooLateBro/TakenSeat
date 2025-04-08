package com.taken_seat.payment_service.presentation.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.payment_service.application.dto.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public ResponseEntity<?> registerPayment(@Valid @RequestBody PaymentRegisterReqDto paymentRegisterReqDto,
		BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(convertBindingErrors(bindingResult));
		}

		return ResponseEntity.status(HttpStatus.OK)
			.body(paymentService.registerPayment(paymentRegisterReqDto));
	}

	private List<Map<String, String>> convertBindingErrors(BindingResult bindingResult) {
		return bindingResult.getFieldErrors().stream()
			.map(error -> Map.of("message", error.getDefaultMessage()))
			.collect(Collectors.toList());
	}
}
