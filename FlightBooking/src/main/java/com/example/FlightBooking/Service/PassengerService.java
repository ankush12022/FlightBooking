package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.PassengerDTO;
import com.example.FlightBooking.Exception.ResourceNotFoundException;
import com.example.FlightBooking.Model.Booking;
import com.example.FlightBooking.Model.Passenger;
import com.example.FlightBooking.Repository.BookingRepository;
import com.example.FlightBooking.Repository.PassengerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final MapperService mapperService;

    public PassengerService(PassengerRepository passengerRepository, BookingRepository bookingRepository, MapperService mapperService) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.mapperService = mapperService;
    }

    public PassengerDTO addPassenger(PassengerDTO passengerDTO) {
        Booking booking = bookingRepository.findById(passengerDTO.getBookingId()).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        Passenger passenger = new Passenger();
        passenger.setBooking(booking);
        passenger.setName(passengerDTO.getName());
        passenger.setAge(passengerDTO.getAge());
        passenger.setSeatNumber(passengerDTO.getSeatNumber());
        return mapperService.toPassengerDTO(passengerRepository.save(passenger));
    }

    public List<PassengerDTO> getAllPassengers() {
        return passengerRepository.findAll().stream().map(mapperService::toPassengerDTO).toList();
    }

    public PassengerDTO getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));
        return mapperService.toPassengerDTO(passenger);
    }

    public List<PassengerDTO> getPassengersByBookingId(Long bookingId) {
        return passengerRepository.findByBookingId(bookingId).stream().map(mapperService::toPassengerDTO).toList();
    }
}
