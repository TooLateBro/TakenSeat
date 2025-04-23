package com.taken_seat.performance_service.performance.domain.model;

import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.performance_service.performance.application.dto.command.UpdateSeatPriceCommand;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

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
@Table(name = "p_performance_seat_prices")
@Entity
public class PerformanceSeatPrice extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "performance_schedule_id", nullable = false)
	private PerformanceSchedule performanceSchedule;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SeatType seatType;

	@Column(nullable = false)
	private Integer price;

	public void update(UpdateSeatPriceCommand command) {

		this.seatType = command.seatType();
		this.price = command.price();
	}
}
