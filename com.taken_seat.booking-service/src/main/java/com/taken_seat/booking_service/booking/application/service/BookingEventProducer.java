package com.taken_seat.booking_service.booking.application.service;

import com.taken_seat.booking_service.booking.domain.Booking;

public interface BookingEventProducer {
	void sendBookingCreatedEvent(Booking booking, int price);
}