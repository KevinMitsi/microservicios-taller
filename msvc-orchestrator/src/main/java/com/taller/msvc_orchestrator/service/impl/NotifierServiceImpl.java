package com.taller.msvc_orchestrator.service.impl;

import com.taller.msvc_orchestrator.DTO.NotificationDTO;
import com.taller.msvc_orchestrator.service.NotifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotifierServiceImpl implements NotifierService {
    private final RabbitTemplate rabbitTemplate;



    @Override
    public void sendNotification(String chanel, String destination, String message) {
        NotificationDTO notification = new NotificationDTO(chanel, destination, message);
        rabbitTemplate.convertAndSend("exchange.notifications", chanel, notification);
    }
}
