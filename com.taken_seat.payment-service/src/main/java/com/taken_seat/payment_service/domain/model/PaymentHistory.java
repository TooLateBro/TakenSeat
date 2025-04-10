package com.taken_seat.payment_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
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
	private Integer price;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	private LocalDateTime approvedAt;

	private Integer refundAmount;

	private LocalDateTime refundRequestedAt;

	public static PaymentHistory register(Payment payment) {
		PaymentHistory paymentHistory = PaymentHistory.builder()
			.payment(payment)
			.price(payment.getPrice())
			.paymentStatus(payment.getPaymentStatus())
			.approvedAt(payment.getApprovedAt())
			.build();

		paymentHistory.prePersist(payment.getCreatedBy());

		return paymentHistory;
	}

	public void update(Payment payment) {
		this.price = payment.getPrice();
		this.paymentStatus = payment.getPaymentStatus();
		this.preUpdate(payment.getUpdatedBy());
	}

	@Override
	public void delete(UUID deleteBy) {
		super.delete(deleteBy);
		this.paymentStatus = PaymentStatus.DELETED;
	}
}
