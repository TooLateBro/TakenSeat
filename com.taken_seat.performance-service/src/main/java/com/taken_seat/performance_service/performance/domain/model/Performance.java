package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;

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
public class Performance {

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

	public static Performance create(CreateRequestDto request) {

		Performance performance = Performance.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.startAt(request.getStartAt())
			.endAt(request.getEndAt())
			.status(PerformanceStatus.status(request.getStartAt(), request.getEndAt()))
			.posterUrl(request.getPosterUrl())
			.ageLimit(request.getAgeLimit())
			.maxTicketCount(request.getMaxTicketCount())
			.discountInfo(request.getDiscountInfo())
			.build();

		List<PerformanceSchedule> schedules = request.getSchedules().stream()
			.map(createPerformanceScheduleDto -> {
				PerformanceSchedule schedule = PerformanceSchedule.builder()
					.performance(performance)
					.performanceHallId(createPerformanceScheduleDto.getPerformanceHallId())
					.startAt(createPerformanceScheduleDto.getStartAt())
					.endAt(createPerformanceScheduleDto.getEndAt())
					.saleStartAt(createPerformanceScheduleDto.getSaleStartAt())
					.saleEndAt(createPerformanceScheduleDto.getSaleEndAt())
					.status(PerformanceScheduleStatus.status(
						createPerformanceScheduleDto.getSaleStartAt(),
						createPerformanceScheduleDto.getSaleEndAt(),
						false
					))
					.build();

				List<PerformanceSeatPrice> seatPrices = createPerformanceScheduleDto.getSeatPrices().stream()
					.map(CreateSeatPriceDto -> PerformanceSeatPrice.builder()
						.performanceSchedule(schedule)
						.seatType(CreateSeatPriceDto.getSeatType())
						.price(CreateSeatPriceDto.getPrice())
						.build())
					.toList();

				schedule.getSeatPrices().addAll(seatPrices);
				return schedule;
			})
			.toList();

		performance.getSchedules().addAll(schedules);
		return performance;
	}
}
