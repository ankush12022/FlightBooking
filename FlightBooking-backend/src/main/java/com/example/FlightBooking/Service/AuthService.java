package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.AuthResponseDTO;
import com.example.FlightBooking.DTO.LoginDTO;
import com.example.FlightBooking.DTO.RegisterDTO;
import com.example.FlightBooking.Exception.EmailAlreadyExistsException;
import com.example.FlightBooking.Model.Role;
import com.example.FlightBooking.Model.User;
import com.example.FlightBooking.Repository.UserRepository;
import com.example.FlightBooking.Security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String register(RegisterDTO registerDTO) {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(resolveRole(registerDTO.getRole()));

        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponseDTO login(LoginDTO loginDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        User user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }

    private Role resolveRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.USER;
        }
        return Role.valueOf(role.trim().toUpperCase());
    }
}
