package com.taken_seat.payment_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "p_payment_history")
public class PaymentHistory extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	@Column(nullable = false)
	private Integer amount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	private LocalDateTime approvedAt;

	private Integer refundAmount;

	private LocalDateTime refundRequestedAt;

	public static PaymentHistory register(Payment payment) {
		PaymentHistory paymentHistory = PaymentHistory.builder()
			.payment(payment)
			.amount(payment.getAmount())
			.paymentStatus(payment.getPaymentStatus())
			.approvedAt(payment.getApprovedAt())
			.build();

		paymentHistory.prePersist(payment.getCreatedBy());

		return paymentHistory;
	}

	public void updateHistory(Payment payment) {
		this.amount = payment.getAmount();
		this.paymentStatus = payment.getPaymentStatus();
		this.approvedAt = payment.getApprovedAt();
		this.refundAmount = payment.getRefundAmount();
		this.refundRequestedAt = payment.getRefundRequestedAt();
		this.preUpdate(payment.getUpdatedBy());
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

	public void refund(Payment payment) {
		if (this.paymentStatus != PaymentStatus.COMPLETED) {
			throw new PaymentException(ResponseCode.CANNOT_REFUND);
		}

		this.refundAmount = payment.getAmount();
		this.refundRequestedAt = LocalDateTime.now();
		this.paymentStatus = PaymentStatus.REFUNDED;
		this.preUpdate(payment.getUpdatedBy());
	}

}
