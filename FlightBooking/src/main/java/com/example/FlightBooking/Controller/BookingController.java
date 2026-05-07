package com.example.FlightBooking.Controller;

import com.example.FlightBooking.DTO.BookingDTO;
import com.example.FlightBooking.Service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public BookingDTO createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        return bookingService.createBooking(bookingDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<BookingDTO> getAllBookings(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingService.getAllBookings(pageable);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public BookingDTO getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}/cancel")
    public BookingDTO cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }
}
