package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.NotificationDTO;
import com.example.FlightBooking.DTO.PaymentDTO;
import com.example.FlightBooking.Exception.BadRequestException;
import com.example.FlightBooking.Exception.InvalidPaymentException;
import com.example.FlightBooking.Exception.ResourceNotFoundException;
import com.example.FlightBooking.Model.*;
import com.example.FlightBooking.Repository.BookingRepository;
import com.example.FlightBooking.Repository.PaymentRepository;
import com.example.FlightBooking.Repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final MapperService mapperService;
    private final RabbitMQProducer rabbitMQProducer;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository, TicketRepository ticketRepository, MapperService mapperService, RabbitMQProducer rabbitMQProducer) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.mapperService = mapperService;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @Transactional
    public PaymentDTO makePayment(PaymentDTO paymentDTO) {
        Booking booking = bookingRepository.findById(paymentDTO.getBookingId()).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Payment cannot be done for cancelled booking");
        }
        if (paymentRepository.existsByBookingId(booking.getId())) {
            throw new InvalidPaymentException("Payment already completed for this booking");
        }
        if (paymentDTO.getAmount() < booking.getTotalCost()) {
            throw new InvalidPaymentException("Payment amount is less than booking total cost");
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentDTO.getAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPayment(payment);

        Ticket ticket = new Ticket();
        ticket.setBooking(booking);
        ticket.setTicketNumber("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        ticket.setIssueDate(LocalDateTime.now());
        booking.setTicket(ticket);

        bookingRepository.save(booking);
        ticketRepository.save(ticket);
        Payment savedPayment = paymentRepository.save(payment);

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserId(booking.getUser().getId());
        notificationDTO.setMessage("Booking confirmed. Ticket generated: " + ticket.getTicketNumber());
        rabbitMQProducer.sendNotification(notificationDTO);

        return mapperService.toPaymentDTO(savedPayment);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(mapperService::toPaymentDTO).toList();
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return mapperService.toPaymentDTO(payment);
    }
}
