package com.taken_seat.booking_service.booking.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_benefit_usage_history")
public class BenefitUsageHistory extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private UUID bookingId;

	private UUID couponId;
	private Integer mileage;
	private LocalDateTime usedAt;
	private boolean refunded;

	public void refunded() {
		this.refunded = true;
	}
}