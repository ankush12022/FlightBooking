package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.NotificationDTO;
import com.example.FlightBooking.Exception.ResourceNotFoundException;
import com.example.FlightBooking.Model.Notification;
import com.example.FlightBooking.Model.User;
import com.example.FlightBooking.Repository.NotificationRepository;
import com.example.FlightBooking.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final MapperService mapperService;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository, MapperService mapperService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.mapperService = mapperService;
    }

    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        User user = userRepository.findById(notificationDTO.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(notificationDTO.getMessage());
        notification.setCreatedAt(LocalDateTime.now());
        return mapperService.toNotificationDTO(notificationRepository.save(notification));
    }

    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream().map(mapperService::toNotificationDTO).toList();
    }

    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId).stream().map(mapperService::toNotificationDTO).toList();
    }
}
