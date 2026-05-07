package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.*;
import com.example.FlightBooking.Model.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MapperService {

    public FlightDTO toFlightDTO(Flight flight) {
        FlightDTO dto = new FlightDTO();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setSource(flight.getSource());
        dto.setDestination(flight.getDestination());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setTotalSeats(flight.getTotalSeats());
        dto.setAvailableSeats(flight.getAvailableSeats());
        dto.setBaseFare(flight.getBaseFare());
        return dto;
    }

    public UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        return dto;
    }

    public PassengerDTO toPassengerDTO(Passenger passenger) {
        PassengerDTO dto = new PassengerDTO();
        dto.setId(passenger.getId());
        dto.setName(passenger.getName());
        dto.setAge(passenger.getAge());
        dto.setSeatNumber(passenger.getSeatNumber());
        if (passenger.getBooking() != null) {
            dto.setBookingId(passenger.getBooking().getId());
        }
        return dto;
    }

    public BookingDTO toBookingDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus().name());
        dto.setTaxes(booking.getTaxes());
        dto.setSeatCharges(booking.getSeatCharges());
        dto.setTotalCost(booking.getTotalCost());
        if (booking.getUser() != null) {
            dto.setUserId(booking.getUser().getId());
        }
        if (booking.getFlight() != null) {
            dto.setFlightId(booking.getFlight().getId());
        }
        if (booking.getPassengers() != null) {
            List<PassengerDTO> passengers = booking.getPassengers().stream().map(this::toPassengerDTO).toList();
            dto.setPassengers(passengers);
        }
        return dto;
    }

    public PaymentDTO toPaymentDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus().name());
        dto.setPaymentDate(payment.getPaymentDate());
        if (payment.getBooking() != null) {
            dto.setBookingId(payment.getBooking().getId());
        }
        return dto;
    }

    public TicketDTO toTicketDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setIssueDate(ticket.getIssueDate());
        if (ticket.getBooking() != null) {
            dto.setBookingId(ticket.getBooking().getId());
        }
        return dto;
    }

    public NotificationDTO toNotificationDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setCreatedAt(notification.getCreatedAt());
        if (notification.getUser() != null) {
            dto.setUserId(notification.getUser().getId());
        }
        return dto;
    }
}
