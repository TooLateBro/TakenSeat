package com.taken_seat.common_service.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {
	private UUID userId;
	private String email;
	private String role;
}