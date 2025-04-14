package com.taken_seat.common_service.message;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserBenefitUsageRequestMessage {

	private UUID userId;

	private UUID couponId;

	private Integer mileage;

}
