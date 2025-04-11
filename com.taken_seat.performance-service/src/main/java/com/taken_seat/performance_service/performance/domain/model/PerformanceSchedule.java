package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.performance_service.performance.application.dto.request.UpdatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.UpdateSeatPriceDto;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_performance_schedules")
@Entity
public class PerformanceSchedule extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "performance_id", nullable = false)
	private Performance performance;

	@Column(name = "performance_hall_id", columnDefinition = "BINARY(16)", nullable = false)
	private UUID performanceHallId;

	@Column(nullable = false)
	private LocalDateTime startAt;

	@Column(nullable = false)
	private LocalDateTime endAt;

	@Column(nullable = false)
	private LocalDateTime saleStartAt;

	private LocalDateTime saleEndAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PerformanceScheduleStatus status;

	@OneToMany(mappedBy = "performanceSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PerformanceSeatPrice> seatPrices = new ArrayList<>();

	public void update(UpdatePerformanceScheduleDto dto) {

		if (dto.getPerformanceHallId() != null)
			this.performanceHallId = dto.getPerformanceHallId();
		if (dto.getStartAt() != null)
			this.startAt = dto.getStartAt();
		if (dto.getEndAt() != null)
			this.endAt = dto.getEndAt();
		if (dto.getSaleStartAt() != null)
			this.saleStartAt = dto.getSaleStartAt();
		if (dto.getSaleEndAt() != null)
			this.saleEndAt = dto.getSaleEndAt();
		if (dto.getStatus() != null)
			this.status = dto.getStatus();

		if (dto.getSeatPrices() != null) {
			for (UpdateSeatPriceDto seatDto : dto.getSeatPrices()) {
				Optional<PerformanceSeatPrice> match = this.seatPrices.stream()
					.filter(savedSeatPrices -> savedSeatPrices.getId().equals(seatDto.getPerformanceSeatPriceId()))
					.findFirst();

				if (match.isPresent()) {
					match.get().update(seatDto);
				} else {
					PerformanceSeatPrice newPrice = PerformanceSeatPrice.builder()
						.performanceSchedule(this)
						.seatType(seatDto.getSeatType())
						.price(seatDto.getPrice())
						.build();
					this.seatPrices.add(newPrice);
				}
			}
		}
	}
}
