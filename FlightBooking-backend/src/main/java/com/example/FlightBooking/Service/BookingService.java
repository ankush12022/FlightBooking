package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.BookingDTO;
import com.example.FlightBooking.DTO.PassengerDTO;
import com.example.FlightBooking.Exception.BadRequestException;
import com.example.FlightBooking.Exception.ResourceNotFoundException;
import com.example.FlightBooking.Exception.SeatNotAvailableException;
import com.example.FlightBooking.Model.*;
import com.example.FlightBooking.Repository.BookingRepository;
import com.example.FlightBooking.Repository.FlightRepository;
import com.example.FlightBooking.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final MapperService mapperService;

    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository, UserRepository userRepository, MapperService mapperService) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.mapperService = mapperService;
    }

    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        User user = userRepository.findById(bookingDTO.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Flight flight = flightRepository.findById(bookingDTO.getFlightId()).orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        List<PassengerDTO> passengerDTOList = bookingDTO.getPassengers();
        if (passengerDTOList == null || passengerDTOList.isEmpty()) {
            throw new BadRequestException("At least one passenger is required");
        }

        int requiredSeats = passengerDTOList.size();
        if (flight.getAvailableSeats() < requiredSeats) {
            throw new SeatNotAvailableException("Required seats are not available");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTaxes(bookingDTO.getTaxes());
        booking.setSeatCharges(bookingDTO.getSeatCharges());
        booking.setTotalCost(calculateTotalCost(flight.getBaseFare(), bookingDTO.getTaxes(), bookingDTO.getSeatCharges(), requiredSeats));

        List<Passenger> passengers = new ArrayList<>();
        for (PassengerDTO passengerDTO : passengerDTOList) {
            Passenger passenger = new Passenger();
            passenger.setName(passengerDTO.getName());
            passenger.setAge(passengerDTO.getAge());
            passenger.setSeatNumber(passengerDTO.getSeatNumber());
            passenger.setBooking(booking);
            passengers.add(passenger);
        }
        booking.setPassengers(passengers);

        flight.setAvailableSeats(flight.getAvailableSeats() - requiredSeats);
        flightRepository.save(flight);

        Booking savedBooking = bookingRepository.save(booking);
        return mapperService.toBookingDTO(savedBooking);
    }

    public Page<BookingDTO> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable).map(mapperService::toBookingDTO);
    }

    public BookingDTO getBookingById(Long id) {
        return mapperService.toBookingDTO(findBookingEntity(id));
    }

    @Transactional
    public BookingDTO cancelBooking(Long id) {
        Booking booking = findBookingEntity(id);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking already cancelled");
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new BadRequestException("Confirmed booking cannot be cancelled after payment in this demo");
        }

        Flight flight = booking.getFlight();
        int passengerCount = booking.getPassengers() == null ? 1 : booking.getPassengers().size();
        flight.setAvailableSeats(flight.getAvailableSeats() + passengerCount);
        flightRepository.save(flight);

        booking.setStatus(BookingStatus.CANCELLED);
        return mapperService.toBookingDTO(bookingRepository.save(booking));
    }

    public Booking findBookingEntity(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private double calculateTotalCost(double baseFare, double taxes, double seatCharges, int passengerCount) {
        return (baseFare + taxes + seatCharges) * passengerCount;
    }
}
