package com.taller.msvc_delivery.services.impl;

import com.taller.msvc_delivery.DTO.NotificationDTO;
import com.taller.msvc_delivery.services.EmailConsumer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConsumerImpl implements EmailConsumer {

    private final JavaMailSender mailSender;

    @Override
    @RabbitListener(queues = "queue.email")
    public void processEmail(NotificationDTO notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(notification.destination());
            helper.setSubject("Notificación del sistema");
            helper.setText(notification.message(), true); // true para HTML

            mailSender.send(message);

            log.info("✅ Email enviado a {}", notification.destination());

        } catch (MessagingException e) {
            log.error("❌ Error al enviar Email: {}", e.getMessage());
        }
    }
}
