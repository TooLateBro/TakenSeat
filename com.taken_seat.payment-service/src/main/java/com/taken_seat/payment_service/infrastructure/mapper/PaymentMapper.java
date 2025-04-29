package com.taken_seat.payment_service.infrastructure.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.payment_service.application.dto.controller.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.dto.controller.request.PaymentUpdateReqDto;
import com.taken_seat.payment_service.application.dto.controller.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.service.PaymentDto;
import com.taken_seat.payment_service.application.dto.service.PaymentSearchDto;
import com.taken_seat.payment_service.domain.model.Payment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

	// Register
	PaymentDto toDto(PaymentRegisterReqDto reqDto, AuthenticatedUser user);

	// Update
	PaymentDto toDto(UUID paymentId, PaymentUpdateReqDto reqDto, AuthenticatedUser user);

	// 결과 반환용
	PaymentDetailResDto toResponse(Payment payment);

	PaymentSearchDto toDto(String q, String category, int page, int size, String sort,
		String order);

}
