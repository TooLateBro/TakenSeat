package com.taken_seat.payment_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.payment_service.application.dto.service.PaymentDto;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_payment")
public class Payment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false)
	private UUID bookingId;

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private Integer price;

	@Column(nullable = false)
	private String orderName;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	private LocalDateTime approvedAt;

	private Integer refundAmount;

	private LocalDateTime refundRequestedAt;

	public static Payment register(PaymentDto dto) {
		Payment payment = Payment.builder()
			.bookingId(dto.getBookingId())
			.userId(dto.getUserId())
			.price(dto.getPrice())
			.orderName(dto.getOrderName())
			.paymentStatus(PaymentStatus.PENDING)
			.build();

		payment.prePersist(dto.getUserId());

		return payment;
	}

	public static Payment register(PaymentMessage message) {
		Payment payment = Payment.builder()
			.bookingId(message.getBookingId())
			.userId(message.getUserId())
			.price(message.getPrice())
			.orderName(message.getOrderName())
			.paymentStatus(PaymentStatus.PENDING)
			.build();

		payment.prePersist(message.getUserId());

		return payment;
	}

	public void update(PaymentDto dto) {
		this.price = dto.getPrice();
		this.paymentStatus = dto.getPaymentStatus();
		this.preUpdate(dto.getUserId());
	}

	public void markAsCompleted(UUID userId) {
		this.paymentStatus = PaymentStatus.COMPLETED;
		this.approvedAt = LocalDateTime.now();
		this.preUpdate(userId);
	}

	@Override
	public void delete(UUID deleteBy) {
		super.delete(deleteBy);
		this.paymentStatus = PaymentStatus.DELETED;
	}

	public void refund(PaymentRefundMessage message) {
		if (this.paymentStatus != PaymentStatus.COMPLETED) {
			throw new PaymentException(ResponseCode.CANNOT_REFUND);
		}

		this.refundAmount = message.getPrice();
		this.refundRequestedAt = LocalDateTime.now();
		this.paymentStatus = PaymentStatus.REFUNDED;
		this.preUpdate(message.getUserId());
	}
}
