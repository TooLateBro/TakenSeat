package com.taken_seat.booking_service.booking.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "p_booking_command")
public class BookingCommand extends BaseTimeEntity {

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
	private UUID scheduleSeatId;

	private UUID paymentId;

	private int price;

	private int discountedPrice;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private BookingStatus bookingStatus = BookingStatus.PENDING;

	private LocalDateTime bookedAt;

	private LocalDateTime canceledAt;

	public void cancel(UUID id) {
		this.preUpdate(id);
		this.canceledAt = LocalDateTime.now();
		this.bookingStatus = BookingStatus.CANCELED;
	}

	public void discount(int price) {
		this.discountedPrice = price;
	}

	public void paymentComplete(UUID paymentId) {
		this.bookingStatus = BookingStatus.COMPLETED;
		this.paymentId = paymentId;
		this.bookedAt = LocalDateTime.now();
	}
}