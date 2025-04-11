package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.booking_service.booking.domain.Booking;

public interface BookingAdminJpaRepository extends JpaRepository<Booking, UUID> {
}