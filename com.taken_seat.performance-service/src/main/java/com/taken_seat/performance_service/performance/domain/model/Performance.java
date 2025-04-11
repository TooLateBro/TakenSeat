package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.request.UpdatePerformanceScheduleDto;
import com.taken_seat.performance_service.performance.application.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.request.UpdateSeatPriceDto;
import com.taken_seat.performance_service.performance.domain.helper.PerformanceCreateHelper;

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

		Performance performance = PerformanceCreateHelper.createPerformance(request);

		performance.prePersist(createdBy);

		List<PerformanceSchedule> schedules = PerformanceCreateHelper.createPerformanceSchedules(request, performance,
			createdBy);

		performance.getSchedules().addAll(schedules);

		return performance;
	}

	public void update(UpdateRequestDto request) {

		if (request.getTitle() != null)
			this.title = request.getTitle();
		if (request.getDescription() != null)
			this.description = request.getDescription();
		if (request.getStartAt() != null)
			this.startAt = request.getStartAt();
		if (request.getEndAt() != null)
			this.endAt = request.getEndAt();
		if (request.getStatus() != null)
			this.status = request.getStatus();
		if (request.getPosterUrl() != null)
			this.posterUrl = request.getPosterUrl();
		if (request.getAgeLimit() != null)
			this.ageLimit = request.getAgeLimit();
		if (request.getMaxTicketCount() != null)
			this.maxTicketCount = request.getMaxTicketCount();
		if (request.getDiscountInfo() != null)
			this.discountInfo = request.getDiscountInfo();

		if (request.getSchedules() != null) {
			for (UpdatePerformanceScheduleDto scheduleDto : request.getSchedules()) {
				Optional<PerformanceSchedule> matchedSchedule = this.schedules.stream()
					.filter(savedSchedule -> savedSchedule.getId().equals(scheduleDto.getPerformanceScheduleId()))
					.findFirst();

				if (matchedSchedule.isPresent()) {
					matchedSchedule.get().update(scheduleDto);
				} else {
					PerformanceSchedule newSchedule = PerformanceSchedule.builder()
						.performance(this)
						.performanceHallId(scheduleDto.getPerformanceHallId())
						.startAt(scheduleDto.getStartAt())
						.endAt(scheduleDto.getEndAt())
						.saleStartAt(scheduleDto.getSaleStartAt())
						.saleEndAt(scheduleDto.getSaleEndAt())
						.status(scheduleDto.getStatus())
						.build();

					if (scheduleDto.getSeatPrices() != null) {
						for (UpdateSeatPriceDto seatPriceDto : scheduleDto.getSeatPrices()) {
							PerformanceSeatPrice newPrice = PerformanceSeatPrice.builder()
								.performanceSchedule(newSchedule)
								.seatType(seatPriceDto.getSeatType())
								.price(seatPriceDto.getPrice())
								.build();

							newSchedule.getSeatPrices().add(newPrice);
						}
					}

					this.schedules.add(newSchedule);
				}
			}
		}
	}

	public PerformanceSchedule getScheduleById(UUID performanceScheduleId) {
		return schedules.stream()
			.filter(performanceSchedule -> performanceSchedule.getId().equals(performanceScheduleId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("공연 회차가 존재하지 않습니다"));
	}
}
