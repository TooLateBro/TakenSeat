package com.taken_seat.booking_service.ticket.domain;

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
@Table(name = "p_ticket")
public class Ticket extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private UUID bookingId;

	@Column(length = 100)
	private String title;

	@Column(length = 100)
	private String name;

	@Column(length = 500)
	private String address;

	private LocalDateTime startAt;

	private LocalDateTime endAt;

	@Column(length = 10)
	private String seatRowNumber;

	@Column(length = 10)
	private String seatNumber;

	@Column(length = 10)
	private String seatType;
}