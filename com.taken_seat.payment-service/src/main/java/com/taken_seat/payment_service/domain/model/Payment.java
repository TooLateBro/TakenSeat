package com.taken_seat.payment_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.taken_seat.payment_service.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private UUID bookingId;

	@Column(nullable = false)
	private Integer price;


	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	private LocalDateTime approvedAt;

	private Integer refundAmount;

	private LocalDateTime refundRequestedAt;

	@CreatedDate
	@Column(updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime createdAt;

	@Column(updatable = false, nullable = false)
	private UUID createdBy;

	@LastModifiedDate
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime updatedAt;

	@Column
	private UUID updatedBy;

	@Column
	private LocalDateTime deletedAt;

	@Column
	private UUID deletedBy;

	@PrePersist
	protected void onCreate() {
		LocalDateTime time = LocalDateTime.now();
		this.createdAt = time;
		this.updatedAt = time;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}
