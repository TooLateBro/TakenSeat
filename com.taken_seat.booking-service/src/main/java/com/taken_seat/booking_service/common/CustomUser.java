package com.taken_seat.booking_service.common;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUser {
	private UUID userId;
	private String email;
	private String role;
}