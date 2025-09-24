package com.taller.msvc_security.Services.Implementation;

import com.taller.msvc_security.Models.UserEvent;
import com.taller.msvc_security.Services.UserEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventServiceImpl implements UserEventService {

    public static final String USER_EVENTS_EXCHANGE = "exchange.user-events";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishEvent(UserEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                USER_EVENTS_EXCHANGE,
                event.getEventType(),
                event
            );
            log.info("Evento publicado: type={}, userId={}, username={}",
                    event.getEventType(),
                    event.getUserId(),
                    event.getUsername());
        } catch (Exception e) {
            log.error("Error al publicar evento: type={}, userId={}, error={}",
                    event.getEventType(),
                    event.getUserId(),
                    e.getMessage());
        }
    }
}
