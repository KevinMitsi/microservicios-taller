package com.taller.msvc_delivery.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "exchange.notifications";

    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Queue emailQueue() {
        return QueueBuilder.durable("queue.email").build();
    }
    @Bean
    Binding bindEmail(Queue emailQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(emailQueue).to(notificationExchange).with("email");
    }

    @Bean
    Queue smsQueue() { return QueueBuilder.durable("queue.sms").build(); }
    @Bean
    Binding bindSms(Queue smsQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(smsQueue).to(notificationExchange).with("sms");
    }

}

