package com.taller.msvc_delivery.services.impl;

import com.taller.msvc_delivery.DTO.NotificationDTO;
import com.taller.msvc_delivery.services.SmsConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class SmsConsumerImpl implements SmsConsumer {
    @Override
    @RabbitListener(queues = "queue.sms")
    public void processSms(NotificationDTO notification) {

    }
}
