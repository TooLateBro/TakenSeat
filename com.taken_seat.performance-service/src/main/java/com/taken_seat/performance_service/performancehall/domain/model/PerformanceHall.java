package com.taken_seat.performance_service.performancehall.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performancehall.application.dto.request.CreateRequestDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "p_performance_halls")
@Entity
public class PerformanceHall {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, length = 500)
	private String address;

	@Column(nullable = false)
	private Integer totalSeats;

	private String description;

	@OneToMany(mappedBy = "performanceHall", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Seat> seats = new ArrayList<>();

	@Column(name = "deleted_by")
	private UUID deletedBy;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public static PerformanceHall create(CreateRequestDto request) {

		PerformanceHall performanceHall = PerformanceHall.builder()
			.name(request.getName())
			.address(request.getAddress())
			.totalSeats(request.getTotalSeats())
			.description(request.getDescription())
			.build();

		List<Seat> seats = request.getSeats().stream()
			.map(createSeatDto -> Seat.builder()
				.performanceHall(performanceHall)
				.rowNumber(createSeatDto.getRowNumber())
				.seatNumber(createSeatDto.getSeatNumber())
				.seatType(createSeatDto.getSeatType())
				.status(createSeatDto.getStatus())
				.build())
			.toList();

		performanceHall.getSeats().addAll(seats);

		return performanceHall;
	}

	public void softDelete(UUID userId) {

		this.deletedBy = userId;
		this.deletedAt = LocalDateTime.now();
	}

	public Seat getSeatById(UUID seatId) {
		return this.getSeats().stream()
			.filter(seat -> seat.getId().equals(seatId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("해당 좌석이 존재하지 않습니다"));
	}
}
