package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.application.dto.command.UpdatePerformanceCommand;
import com.taken_seat.performance_service.performance.domain.helper.PerformanceUpdateHelper;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "p_performances")
@Entity
public class Performance extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private LocalDateTime startAt;

	@Column(nullable = false)
	private LocalDateTime endAt;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PerformanceStatus status;

	private String posterUrl;

	@Column(length = 100)
	private String ageLimit;

	private Integer maxTicketCount;

	private String discountInfo;

	@OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PerformanceSchedule> schedules = new ArrayList<>();

	public void addSchedules(List<PerformanceSchedule> schedules) {
		this.schedules.addAll(schedules);
	}

	public void update(UpdatePerformanceCommand command, UUID updatedBy) {
		this.preUpdate(updatedBy);

		title = command.title();
		description = command.description();
		startAt = command.startAt();
		endAt = command.endAt();
		status = command.status();
		posterUrl = command.posterUrl();
		ageLimit = command.ageLimit();
		maxTicketCount = command.maxTicketCount();
		discountInfo = command.discountInfo();

		PerformanceUpdateHelper.updateSchedules(this, command.schedules(), updatedBy);
	}

	public PerformanceSchedule getScheduleById(UUID performanceScheduleId) {
		return schedules.stream()
			.filter(performanceSchedule -> performanceSchedule.getId().equals(performanceScheduleId))
			.findFirst()
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_SCHEDULE_NOT_FOUND_EXCEPTION));
	}

	public Integer findPriceByScheduleAndSeatType(UUID performanceScheduleId, SeatType seatType) {
		return this.getScheduleById(performanceScheduleId)
			.getScheduleSeats().stream()
			.filter(scheduleSeat -> scheduleSeat.getSeatType().equals(seatType))
			.map(ScheduleSeat::getPrice)
			.findFirst()
			.orElseThrow(() -> new PerformanceException(ResponseCode.SEAT_PRICE_NOT_FOUND_EXCEPTION));
	}

	public void updateStatus(PerformanceStatus newStatus) {
		this.status = newStatus;
	}

	public void updateScheduleStatus(UUID performanceScheduleId) {
		PerformanceSchedule schedule = getScheduleById(performanceScheduleId);
		schedule.updateStatusBasedOnSeats();
	}

	public void updateStatusBasedOnSchedules() {
		this.status = PerformanceStatus.status(startAt, endAt, schedules);
	}
}
