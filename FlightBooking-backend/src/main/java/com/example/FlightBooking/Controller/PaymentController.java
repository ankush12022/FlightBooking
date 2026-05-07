package com.example.FlightBooking.Controller;

import com.example.FlightBooking.DTO.PaymentDTO;
import com.example.FlightBooking.Service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public PaymentDTO makePayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return paymentService.makePayment(paymentDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public PaymentDTO getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }
}
