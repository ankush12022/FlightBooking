package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.UserDTO;
import com.example.FlightBooking.Exception.ResourceNotFoundException;
import com.example.FlightBooking.Model.User;
import com.example.FlightBooking.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MapperService mapperService;

    public UserService(UserRepository userRepository, MapperService mapperService) {
        this.userRepository = userRepository;
        this.mapperService = mapperService;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(mapperService::toUserDTO).toList();
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapperService.toUserDTO(user);
    }
}
