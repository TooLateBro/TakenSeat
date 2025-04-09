package com.taken_seat.booking_service.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.domain.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
}