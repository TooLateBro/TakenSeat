package com.taken_seat.performance_service.performancehall.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
}
