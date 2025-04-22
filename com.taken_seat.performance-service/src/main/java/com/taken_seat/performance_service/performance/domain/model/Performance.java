package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performance.domain.helper.PerformanceCreateHelper;
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

	public static Performance create(CreateRequestDto request, UUID createdBy) {

		Performance performance = PerformanceCreateHelper.createPerformance(request, createdBy);

		PerformanceCreateHelper.createPerformanceSchedules(request, performance,
			createdBy);

		return performance;
	}

	public void update(UpdateRequestDto request, UUID updatedBy) {
		this.preUpdate(updatedBy);

		this.title = request.getTitle();
		this.description = request.getDescription();
		this.startAt = request.getStartAt();
		this.endAt = request.getEndAt();
		this.status = request.getStatus();
		this.posterUrl = request.getPosterUrl();
		this.ageLimit = request.getAgeLimit();
		this.maxTicketCount = request.getMaxTicketCount();
		this.discountInfo = request.getDiscountInfo();

		PerformanceUpdateHelper.updateSchedules(this, request.getSchedules(), updatedBy);
	}

	public PerformanceSchedule getScheduleById(UUID performanceScheduleId) {
		return schedules.stream()
			.filter(performanceSchedule -> performanceSchedule.getId().equals(performanceScheduleId))
			.findFirst()
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_SCHEDULE_NOT_FOUND_EXCEPTION));
	}

	public Integer findPriceByScheduleAndSeatType(UUID performanceScheduleId, SeatType seatType) {
		return this.schedules.stream()
			.filter(performanceSchedule -> performanceSchedule.getId().equals(performanceScheduleId))
			.flatMap(performanceSchedule -> performanceSchedule.getSeatPrices().stream())
			.filter(performanceSeatPrice -> performanceSeatPrice.getSeatType().equals(seatType))
			.map(PerformanceSeatPrice::getPrice)
			.findFirst()
			.orElseThrow(() -> new PerformanceException(ResponseCode.SEAT_PRICE_NOT_FOUND_EXCEPTION));
	}

	public void updateStatus(PerformanceStatus newStatus) {
		this.status = newStatus;
	}

	public void updateScheduleStatus(UUID performanceScheduleId, PerformanceScheduleStatus newStatus) {
		PerformanceSchedule schedule = getScheduleById(performanceScheduleId);
		schedule.updateStatus(newStatus);
	}
}
