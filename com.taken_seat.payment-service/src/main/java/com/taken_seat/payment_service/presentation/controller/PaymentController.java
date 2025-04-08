package com.taken_seat.payment_service.presentation.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.payment_service.application.dto.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.dto.request.PaymentUpdateReqDto;
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

	@GetMapping("/{id}")
	public ResponseEntity<?> getPaymentDetail(@PathVariable("id") UUID id) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(paymentService.getPaymentDetail(id));
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchPayment(@RequestParam(required = false) String q,
		@RequestParam(required = false) String category,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sort,
		@RequestParam(defaultValue = "desc") String order) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(paymentService.searchPayment(q, category, page, size, sort, order));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> updatePayment(@PathVariable("id") UUID id,
		@Valid @RequestBody PaymentUpdateReqDto paymentUpdateReqDto,
		BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(convertBindingErrors(bindingResult));
		}

		return ResponseEntity.status(HttpStatus.OK)
			.body(paymentService.updatePayment(id, paymentUpdateReqDto));

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePayment(@PathVariable("id") UUID id) {
		paymentService.deletePayment(id);
		return ResponseEntity.status(HttpStatus.OK)
			.build();
	}

	private List<Map<String, String>> convertBindingErrors(BindingResult bindingResult) {
		return bindingResult.getFieldErrors().stream()
			.map(error -> Map.of("message", error.getDefaultMessage()))
			.collect(Collectors.toList());
	}
}
