package com.taller.msvc_delivery.services.impl;

import com.taller.msvc_delivery.DTO.NotificationDTO;
import com.taller.msvc_delivery.services.SmsConsumer;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsConsumerImpl implements SmsConsumer {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromNumber;

    @Override
    @RabbitListener(queues = "queue.sms")
    public void processSms(NotificationDTO notification) {
        try {
            Twilio.init(accountSid, authToken);

            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(notification.getDestination()),
                    new com.twilio.type.PhoneNumber(fromNumber),
                    notification.getMessage()
            ).create();

            log.info("✅ SMS enviado a {} con SID {}", notification.getDestination(), message.getSid());

        } catch (Exception e) {
            log.error("❌ Error al enviar SMS a {}: {}", notification.getDestination(), e.getMessage());
        }
    }
}
