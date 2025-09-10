package com.taller.msvc_delivery.services.impl;

import com.taller.msvc_delivery.DTO.NotificationDTO;
import com.taller.msvc_delivery.services.EmailConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class EmailConsumerImpl implements EmailConsumer {

    @Override
    @RabbitListener(queues = "queue.email")
    public void processEmail(NotificationDTO notification) {

    }
}
