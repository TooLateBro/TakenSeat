package com.taken_seat.payment_service.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.payment_service.application.dto.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.dto.request.PaymentUpdateReqDto;
import com.taken_seat.payment_service.application.dto.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public ResponseEntity<ApiResponseData<PaymentDetailResDto>> registerPayment(
		@Valid @RequestBody PaymentRegisterReqDto paymentRegisterReqDto,
		AuthenticatedUser authenticatedUser) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(paymentService.registerPayment(paymentRegisterReqDto, authenticatedUser)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<PaymentDetailResDto>> getPaymentDetail(@PathVariable("id") UUID id) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(paymentService.getPaymentDetail(id)));
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponseData<PagePaymentResponseDto>> searchPayment(
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String category,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sort,
		@RequestParam(defaultValue = "desc") String order) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(paymentService.searchPayment(q, category, page, size, sort, order)));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseData<PaymentDetailResDto>> updatePayment(@PathVariable("id") UUID id,
		@Valid @RequestBody PaymentUpdateReqDto paymentUpdateReqDto,
		AuthenticatedUser authenticatedUser) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(paymentService.updatePayment(id, paymentUpdateReqDto, authenticatedUser)));

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseData<String>> deletePayment(@PathVariable("id") UUID id,
		AuthenticatedUser authenticatedUser) {
		paymentService.deletePayment(id, authenticatedUser);
		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success("Delete Success"));
	}
}
