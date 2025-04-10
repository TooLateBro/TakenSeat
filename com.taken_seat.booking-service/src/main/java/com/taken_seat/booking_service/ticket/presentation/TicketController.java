package com.taken_seat.booking_service.ticket.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.booking_service.ticket.application.service.TicketService;
import com.taken_seat.booking_service.ticket.application.dto.request.TicketCreateRequest;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketCreateResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
public class TicketController {

	private final TicketService ticketService;

	@PostMapping
	public ResponseEntity<TicketCreateResponse> createTicket(AuthenticatedUser authenticatedUser,
		@RequestBody @Valid TicketCreateRequest request) {

		TicketCreateResponse response = ticketService.createTicket(authenticatedUser, request);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTicket(AuthenticatedUser authenticatedUser, @PathVariable("id") UUID id) {

		ticketService.deleteTicket(authenticatedUser, id);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}