package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.ReportDTO;
import com.example.FlightBooking.Model.BookingStatus;
import com.example.FlightBooking.Model.PaymentStatus;
import com.example.FlightBooking.Repository.BookingRepository;
import com.example.FlightBooking.Repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public ReportService(BookingRepository bookingRepository, PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    public ReportDTO getReport() {
        long totalBookings = bookingRepository.count();
        long confirmedBookings = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        long cancelledBookings = bookingRepository.countByStatus(BookingStatus.CANCELLED);
        double totalRevenue = paymentRepository.totalRevenueByStatus(PaymentStatus.SUCCESS);
        return new ReportDTO(totalBookings, confirmedBookings, cancelledBookings, totalRevenue);
    }
}
