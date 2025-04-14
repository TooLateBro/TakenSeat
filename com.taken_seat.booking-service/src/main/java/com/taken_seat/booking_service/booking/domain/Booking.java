package com.taken_seat.booking_service.booking.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.booking_service.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Entity
@Getter
@NoArgsConstructor
@Table(
	name = "p_booking",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_upps",
			columnNames = {"userId", "performanceId", "performanceScheduleId", "seatId"}
		)
	}
)
public class Booking extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private UUID performanceId;

	@Column(nullable = false)
	private UUID performanceScheduleId;

	@Column(nullable = false)
	private UUID seatId;

	private UUID paymentId;

	private int price;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private BookingStatus bookingStatus = BookingStatus.PENDING;

	private LocalDateTime bookedAt;

	private LocalDateTime canceledAt;

	public void cancel() {
		this.canceledAt = LocalDateTime.now();
	}
}