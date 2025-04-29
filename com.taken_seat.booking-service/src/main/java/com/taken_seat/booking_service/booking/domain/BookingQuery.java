package com.taken_seat.booking_service.booking.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.booking_service.booking.application.dto.event.BookingEntityEvent;
import com.taken_seat.common_service.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "p_booking_query")
public class BookingQuery extends BaseTimeEntity {
	@Id
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

	@Column(length = 100)
	private String title;

	@Column(length = 100)
	private String name;

	@Column(length = 500)
	private String address;

	@Column(length = 10, name = "seat_row_number")
	private String rowNumber;

	@Column(length = 10)
	private String seatNumber;

	@Column(length = 10)
	private String seatType;

	public void create(BookingEntityEvent event) {
		this.createdAt = event.createdAt();
		this.createdBy = event.createdBy();
	}

	public void update(BookingEntityEvent event) {
		this.paymentId = event.paymentId();
		this.price = event.price();
		this.discountedPrice = event.discountedPrice();
		this.bookingStatus = BookingStatus.valueOf(event.bookingStatus());
		this.bookedAt = event.bookedAt();
		this.canceledAt = event.canceledAt();
		this.updatedAt = event.updatedAt();
		this.updatedBy = event.updatedBy();
		this.deletedAt = event.deletedAt();
		this.deletedBy = event.deletedBy();
	}
}