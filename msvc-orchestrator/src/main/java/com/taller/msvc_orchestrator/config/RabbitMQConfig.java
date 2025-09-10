package com.taller.msvc_orchestrator.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("exchange.notifications");
    }

    @Bean
    public Queue emailQueue() {
        return new Queue("queue.email", true);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue("queue.sms", true);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange exchange) {
        return BindingBuilder.bind(emailQueue).to(exchange).with("email");
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(smsQueue).to(exchange).with("sms");
    }
}
