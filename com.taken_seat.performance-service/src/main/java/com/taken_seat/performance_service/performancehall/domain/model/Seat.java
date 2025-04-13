package com.taken_seat.performance_service.performancehall.domain.model;

import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performancehall.application.dto.request.UpdateSeatDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_seats")
@Entity
public class Seat extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "performance_hall_id", nullable = false)
	private PerformanceHall performanceHall;

	@Column(name = "seat_row_number", nullable = false, length = 10)
	private String rowNumber;

	@Column(nullable = false, length = 10)
	private String seatNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeatType seatType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeatStatus status;

	public void update(UpdateSeatDto seatDto) {
		this.rowNumber = seatDto.getRowNumber();
		this.seatNumber = seatDto.getSeatNumber();
		this.seatType = seatDto.getSeatType();
		this.status = seatDto.getStatus();
	}

	public void updateStatus(SeatStatus newStatus) {

		if (this.status == SeatStatus.DISABLED) {
			throw new PerformanceException(ResponseCode.SEAT_STATUS_CHANGE_NOT_ALLOWED);
		}

		this.status = newStatus;
	}
}
