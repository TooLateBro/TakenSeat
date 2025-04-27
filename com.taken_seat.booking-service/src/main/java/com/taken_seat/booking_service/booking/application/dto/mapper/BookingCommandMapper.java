package com.taken_seat.booking_service.booking.application.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Pageable;

import com.taken_seat.booking_service.booking.application.dto.command.BookingAdminPageReadCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPageReadCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingPayRequest;
import com.taken_seat.common_service.dto.AuthenticatedUser;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookingCommandMapper {

	BookingCreateCommand toCommand(AuthenticatedUser user, BookingCreateRequest request);

	BookingSingleTargetCommand toCommand(AuthenticatedUser user, UUID bookingId);

	BookingPageReadCommand toCommand(AuthenticatedUser user, Pageable pageable);

	BookingPaymentCommand toCommand(AuthenticatedUser user, UUID bookingId, BookingPayRequest request);

	BookingAdminPageReadCommand toCommand(AuthenticatedUser user, UUID queryUserId, Pageable pageable);
}