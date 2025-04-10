package com.taken_seat.booking_service.ticket.presentation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.ticket.application.dto.request.TicketCreateRequest;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketCreateResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketPageResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketReadResponse;
import com.taken_seat.booking_service.ticket.application.service.TicketService;
import com.taken_seat.common_service.dto.AuthenticatedUser;

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

	@GetMapping("/{id}")
	public ResponseEntity<TicketReadResponse> readTicket(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		TicketReadResponse response = ticketService.readTicket(authenticatedUser, id);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<TicketPageResponse> readTickets(AuthenticatedUser authenticatedUser, Pageable pageable) {

		TicketPageResponse response = ticketService.readTickets(authenticatedUser, pageable);

		return ResponseEntity.ok(response);
	}
}