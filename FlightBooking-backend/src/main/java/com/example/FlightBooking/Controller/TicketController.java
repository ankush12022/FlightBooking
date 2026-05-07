package com.example.FlightBooking.Controller;

import com.example.FlightBooking.DTO.TicketDTO;
import com.example.FlightBooking.Service.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public TicketDTO generateTicket(@RequestBody TicketDTO ticketDTO) {
        return ticketService.generateTicket(ticketDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<TicketDTO> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public TicketDTO getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/booking/{bookingId}")
    public TicketDTO getTicketByBookingId(@PathVariable Long bookingId) {
        return ticketService.getTicketByBookingId(bookingId);
    }
}
