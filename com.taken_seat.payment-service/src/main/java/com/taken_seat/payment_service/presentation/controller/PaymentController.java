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

import com.taken_seat.common_service.aop.annotation.RoleCheck;
import com.taken_seat.common_service.aop.vo.Role;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.payment_service.application.dto.controller.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.dto.controller.request.PaymentUpdateReqDto;
import com.taken_seat.payment_service.application.dto.controller.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.controller.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.service.PaymentDto;
import com.taken_seat.payment_service.application.dto.service.PaymentSearchDto;
import com.taken_seat.payment_service.application.service.PaymentService;
import com.taken_seat.payment_service.infrastructure.mapper.PaymentMapper;
import com.taken_seat.payment_service.infrastructure.swagger.PaymentSwaggerDocs;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment API", description = "결제 관련 API")
public class PaymentController {

	private final PaymentService paymentService;
	private final PaymentMapper paymentMapper;

	@PostMapping
	// @RoleCheck(allowedRoles = Role.ADMIN)
	@PaymentSwaggerDocs.RegisterPayment
	public ResponseEntity<ApiResponseData<PaymentDetailResDto>> registerPayment(
		@Valid @RequestBody PaymentRegisterReqDto paymentRegisterReqDto,
		AuthenticatedUser authenticatedUser) {

		PaymentDto dto = paymentMapper.toDto(paymentRegisterReqDto, authenticatedUser);

		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(paymentService.registerPayment(dto)));
	}

	@GetMapping("/{paymentId}")
	@PaymentSwaggerDocs.GetPaymentDetail
	public ResponseEntity<ApiResponseData<PaymentDetailResDto>> getPaymentDetail(
		@PathVariable("paymentId") UUID paymentId) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(paymentService.getPaymentDetail(paymentId)));
	}

	@GetMapping("/search")
	@PaymentSwaggerDocs.SearchPayment
	public ResponseEntity<ApiResponseData<PagePaymentResponseDto>> searchPayment(
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String category,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sort,
		@RequestParam(defaultValue = "desc") String order) {

		PaymentSearchDto dto = paymentMapper.toDto(q, category, page, size, sort, order);

		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(paymentService.searchPayment(dto)));
	}

	@PatchMapping("/{paymentId}")
	@RoleCheck(allowedRoles = Role.ADMIN)
	@PaymentSwaggerDocs.UpdatePayment
	public ResponseEntity<ApiResponseData<PaymentDetailResDto>> updatePayment(@PathVariable("paymentId") UUID paymentId,
		@Valid @RequestBody PaymentUpdateReqDto paymentUpdateReqDto,
		AuthenticatedUser authenticatedUser) {

		PaymentDto dto = paymentMapper.toDto(paymentId, paymentUpdateReqDto, authenticatedUser);

		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(paymentService.updatePayment(dto)));

	}

	@DeleteMapping("/{paymentId}")
	@RoleCheck(allowedRoles = Role.ADMIN)
	@PaymentSwaggerDocs.DeletePayment
	public ResponseEntity<ApiResponseData<String>> deletePayment(@PathVariable("paymentId") UUID paymentId,
		AuthenticatedUser authenticatedUser) {
		paymentService.deletePayment(paymentId, authenticatedUser);
		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success("Delete Success"));
	}
}
