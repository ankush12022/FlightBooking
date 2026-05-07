package com.example.FlightBooking.Repository;

import com.example.FlightBooking.Model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {
    List<Passenger> findByBookingId(Long bookingId);
}
