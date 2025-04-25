package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceScheduleCommand;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

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
	private List<ScheduleSeat> scheduleSeats = new ArrayList<>();

	public void update(UpdatePerformanceScheduleCommand command) {
		performanceHallId = command.performanceHallId();
		startAt = command.startAt();
		endAt = command.endAt();
		saleStartAt = command.saleStartAt();
		saleEndAt = command.saleEndAt();
		status = command.status();
	}

	public void updateStatus(PerformanceScheduleStatus newStatus) {
		status = newStatus;
	}

	public void addSeats(List<ScheduleSeat> seats) {
		scheduleSeats.addAll(seats);
	}

	public ScheduleSeat getScheduleSeatById(UUID scheduleSeatId) {
		return scheduleSeats.stream()
			.filter(scheduleSeat -> scheduleSeat.getId().equals(scheduleSeatId))
			.findFirst()
			.orElseThrow(() -> new PerformanceException(ResponseCode.SEAT_NOT_FOUND_EXCEPTION));
	}

	public boolean isSoldOut() {
		return scheduleSeats.stream()
			.noneMatch(scheduleSeat -> scheduleSeat.getSeatStatus() == SeatStatus.AVAILABLE);
	}

	public void updateStatusBasedOnSeats() {
		status = PerformanceScheduleStatus.status(saleStartAt, saleEndAt, isSoldOut());

		if (performance != null) {
			performance.updateStatusBasedOnSchedules();
		}
	}
}
