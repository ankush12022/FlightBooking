package com.example.FlightBooking.Controller;

import com.example.FlightBooking.DTO.AuthResponseDTO;
import com.example.FlightBooking.DTO.LoginDTO;
import com.example.FlightBooking.DTO.RegisterDTO;
import com.example.FlightBooking.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }
}
