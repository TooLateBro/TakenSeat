package com.taken_seat.payment_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.taken_seat.payment_service.domain.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	private Integer price;

	@Column(nullable = false)
	private PaymentStatus payment_status;

	private LocalDateTime approved_at;

	private Integer refund_amount;

	private LocalDateTime refund_requested_at;

	@CreatedDate
	@Column(updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime created_at;

	@Column(updatable = false, nullable = false)
	private UUID created_by;

	@LastModifiedDate
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime updated_at;

	@Column
	private UUID updated_by;

	@Column
	private LocalDateTime deleted_at;

	@Column
	private UUID deleted_by;

	@PrePersist
	protected void onCreate() {
		LocalDateTime time = LocalDateTime.now();
		this.created_at = time;
		this.updated_at = time;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updated_at = LocalDateTime.now();
	}

}
