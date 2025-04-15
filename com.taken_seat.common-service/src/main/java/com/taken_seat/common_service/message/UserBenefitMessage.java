package com.taken_seat.common_service.message;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBenefitMessage {

	private UUID bookingId;

	private UUID userId;

	private UUID couponId;

	private Integer mileage;

	private Integer discount;

	private UserBenefitStatus status;

	public enum UserBenefitStatus {
		SUCCESS,
		FAIL
	}

}