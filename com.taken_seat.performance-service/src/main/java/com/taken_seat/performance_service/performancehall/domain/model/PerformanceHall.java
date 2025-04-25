package com.taken_seat.performance_service.performancehall.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performancehall.application.dto.command.CreatePerformanceHallCommand;
import com.taken_seat.performance_service.performancehall.application.dto.command.SeatTemplateInfo;
import com.taken_seat.performance_service.performancehall.application.dto.command.UpdatePerformanceHallCommand;
import com.taken_seat.performance_service.performancehall.application.dto.command.UpdateSeatCommand;
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

	public static PerformanceHall create(CreatePerformanceHallCommand command, UUID createBy) {

		PerformanceHall performanceHall =
			PerformanceHallCreateHelper.createPerformanceHall(command, createBy);

		PerformanceHallCreateHelper.createSeats(command, performanceHall, createBy);

		return performanceHall;
	}

	public Seat getSeatById(UUID seatId) {
		return this.getSeats().stream()
			.filter(seat -> seat.getId().equals(seatId))
			.findFirst()
			.orElseThrow(() -> new PerformanceException(ResponseCode.SEAT_NOT_FOUND_EXCEPTION));
	}

	public void update(UpdatePerformanceHallCommand command) {
		this.updatePerformanceHall(command);
		this.updateSeats(command.seats());
	}

	public void updatePerformanceHall(UpdatePerformanceHallCommand command) {
		this.name = command.name();
		this.address = command.address();
		this.description = command.description();
	}

	public void updateSeats(List<UpdateSeatCommand> seatDtoList) {

		if (seatDtoList == null) {
			return;
		}

		for (UpdateSeatCommand seatDto : seatDtoList) {
			Seat existingSeat = this.getSeatById(seatDto.seatId());

			if (existingSeat != null) {
				existingSeat.update(seatDto);
			} else {
				Seat newSeat = Seat.builder()
					.performanceHall(this)
					.rowNumber(seatDto.rowNumber())
					.seatNumber(seatDto.seatNumber())
					.seatType(seatDto.seatType())
					.status(seatDto.status())
					.build();
				this.seats.add(newSeat);
			}
		}
		this.totalSeats = this.seats.size();
	}

	public boolean isSoldOut() {
		return seats.stream()
			.noneMatch(seat -> seat.getStatus() == SeatStatus.AVAILABLE);
	}

	public List<SeatTemplateInfo> toSeatTemplateInfos() {
		return seats.stream()
			.map(seat -> new SeatTemplateInfo(
				seat.getRowNumber(),
				seat.getSeatNumber(),
				seat.getSeatType(),
				seat.getStatus()
			))
			.toList();
	}
}
