package com.taken_seat.performance_service.performancehall.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.performance_service.performancehall.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.request.UpdateSeatDto;
import com.taken_seat.performance_service.performancehall.domain.helper.PerformanceHallCreateHelper;

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
public class PerformanceHall extends BaseTimeEntity {

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

	public static PerformanceHall create(CreateRequestDto request, UUID createBy) {

		PerformanceHall performanceHall =
			PerformanceHallCreateHelper.createPerformanceHall(request, createBy);

		PerformanceHallCreateHelper.createSeats(request, performanceHall, createBy);

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

	public void update(UpdateRequestDto request) {
		this.updatePerformanceHall(request);
		this.updateSeats(request.getSeats());
	}

	public void updatePerformanceHall(UpdateRequestDto request) {
		this.name = request.getName();
		this.address = request.getAddress();
		this.description = request.getDescription();
	}

	public void updateSeats(List<UpdateSeatDto> seatDtoList) {

		if (seatDtoList == null) {
			return;
		}

		for (UpdateSeatDto seatDto : seatDtoList) {
			Seat existingSeat = this.getSeatById(seatDto.getSeatId());

			if (existingSeat != null) {
				existingSeat.update(seatDto);
			} else {
				Seat newSeat = Seat.builder()
					.performanceHall(this)
					.rowNumber(seatDto.getRowNumber())
					.seatNumber(seatDto.getSeatNumber())
					.seatType(seatDto.getSeatType())
					.status(seatDto.getStatus())
					.build();
				this.seats.add(newSeat);
			}
		}
		this.totalSeats = this.seats.size();

	}
}
