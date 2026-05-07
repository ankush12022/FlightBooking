package com.example.FlightBooking.Repository;

import com.example.FlightBooking.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Optional<Ticket> findByBookingId(Long bookingId);
}
