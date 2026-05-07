package com.example.FlightBooking.Controller;

import com.example.FlightBooking.DTO.PassengerDTO;
import com.example.FlightBooking.Service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public PassengerDTO addPassenger(@Valid @RequestBody PassengerDTO passengerDTO) {
        return passengerService.addPassenger(passengerDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<PassengerDTO> getAllPassengers() {
        return passengerService.getAllPassengers();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public PassengerDTO getPassengerById(@PathVariable Long id) {
        return passengerService.getPassengerById(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/booking/{bookingId}")
    public List<PassengerDTO> getPassengersByBookingId(@PathVariable Long bookingId) {
        return passengerService.getPassengersByBookingId(bookingId);
    }
}
