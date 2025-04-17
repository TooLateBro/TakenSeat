package com.taken_seat.booking_service.ticket.presentation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.ticket.application.dto.response.TicketPageResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketReadResponse;
import com.taken_seat.booking_service.ticket.application.service.TicketService;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
public class TicketController {

	private final TicketService ticketService;

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<TicketReadResponse>> readTicket(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		TicketReadResponse response = ticketService.readTicket(authenticatedUser, id);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponseData<TicketPageResponse>> readTickets(AuthenticatedUser authenticatedUser,
		Pageable pageable) {

		TicketPageResponse response = ticketService.readTickets(authenticatedUser, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> deleteTicket(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		ticketService.deleteTicket(authenticatedUser, id);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}
}