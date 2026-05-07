package com.example.FlightBooking.Service;

import com.example.FlightBooking.DTO.NotificationDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {

    private final NotificationService notificationService;

    public RabbitMQConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receiveNotification(NotificationDTO notificationDTO) {
        notificationService.createNotification(notificationDTO);
    }
}
