package com.example.FlightBooking.Controller;

import com.example.FlightBooking.DTO.ReportDTO;
import com.example.FlightBooking.Service.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ReportDTO getReport() {
        return reportService.getReport();
    }
}
