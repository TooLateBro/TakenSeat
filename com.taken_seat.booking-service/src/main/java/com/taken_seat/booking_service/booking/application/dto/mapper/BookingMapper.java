package com.taken_seat.booking_service.booking.application.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Pageable;

import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.dto.event.BookingEntityEvent;
import com.taken_seat.booking_service.booking.application.dto.query.BookingAdminListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingReadQuery;
import com.taken_seat.booking_service.booking.domain.BookingCommand;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingPayRequest;
import com.taken_seat.common_service.dto.AuthenticatedUser;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BookingMapper {

	BookingCreateCommand toCommand(AuthenticatedUser user, BookingCreateRequest request);

	BookingSingleTargetCommand toCommand(AuthenticatedUser user, UUID bookingId);

	BookingPaymentCommand toCommand(AuthenticatedUser user, UUID bookingId, BookingPayRequest request);

	BookingReadQuery toQuery(AuthenticatedUser user, UUID bookingId);

	BookingListQuery toQuery(AuthenticatedUser user, Pageable pageable);

	BookingAdminListQuery toQuery(AuthenticatedUser user, UUID queryUserId, Pageable pageable);

	BookingEntityEvent toEvent(BookingCommand bookingCommand);
}