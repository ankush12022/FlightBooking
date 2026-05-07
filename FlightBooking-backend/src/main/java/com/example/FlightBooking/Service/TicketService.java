package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.TicketDTO;
import com.example.FlightBooking.Exception.BadRequestException;
import com.example.FlightBooking.Exception.ResourceNotFoundException;
import com.example.FlightBooking.Model.Booking;
import com.example.FlightBooking.Model.BookingStatus;
import com.example.FlightBooking.Model.Ticket;
import com.example.FlightBooking.Repository.BookingRepository;
import com.example.FlightBooking.Repository.TicketRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final MapperService mapperService;

    public TicketService(TicketRepository ticketRepository, BookingRepository bookingRepository, MapperService mapperService) {
        this.ticketRepository = ticketRepository;
        this.bookingRepository = bookingRepository;
        this.mapperService = mapperService;
    }

    public TicketDTO generateTicket(TicketDTO ticketDTO) {
        Booking booking = bookingRepository.findById(ticketDTO.getBookingId()).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Ticket can be generated only after successful payment");
        }
        return ticketRepository.findByBookingId(booking.getId())
                .map(mapperService::toTicketDTO)
                .orElseGet(() -> {
                    Ticket ticket = new Ticket();
                    ticket.setBooking(booking);
                    ticket.setTicketNumber("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    ticket.setIssueDate(LocalDateTime.now());
                    return mapperService.toTicketDTO(ticketRepository.save(ticket));
                });
    }

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream().map(mapperService::toTicketDTO).toList();
    }

    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return mapperService.toTicketDTO(ticket);
    }

    public TicketDTO getTicketByBookingId(Long bookingId) {
        Ticket ticket = ticketRepository.findByBookingId(bookingId).orElseThrow(() -> new ResourceNotFoundException("Ticket not found for this booking"));
        return mapperService.toTicketDTO(ticket);
    }
}
