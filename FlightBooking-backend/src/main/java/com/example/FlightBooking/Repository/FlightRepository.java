package com.example.FlightBooking.Repository;

import com.example.FlightBooking.Model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    Page<Flight> findBySourceIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(
            String source,
            String destination,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}