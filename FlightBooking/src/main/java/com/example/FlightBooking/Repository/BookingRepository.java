package com.example.FlightBooking.Repository;

import com.example.FlightBooking.Model.Booking;
import com.example.FlightBooking.Model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    long countByStatus(BookingStatus status);
}
