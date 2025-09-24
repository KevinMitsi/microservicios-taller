package com.taller.msvc_orchestrator.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String NOTIFICATIONS_EXCHANGE = "exchange.notifications";
    public static final String USER_EVENTS_EXCHANGE = "exchange.user-events";

    // Configuración para notificaciones (ya existente)
    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATIONS_EXCHANGE);
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
    Queue smsQueue() {
        return QueueBuilder.durable("queue.sms").build();
    }

    @Bean
    Binding bindSms(Queue smsQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(smsQueue).to(notificationExchange).with("sms");
    }

    // Nueva configuración para eventos de usuario
    @Bean
    TopicExchange userEventsExchange() {
        return new TopicExchange(USER_EVENTS_EXCHANGE);
    }

    @Bean
    Queue newUserQueue() {
        return QueueBuilder.durable("queue.new-user").build();
    }

    @Bean
    Binding bindNewUser(Queue newUserQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(newUserQueue).to(userEventsExchange).with("new-user");
    }

    @Bean
    Queue loginQueue() {
        return QueueBuilder.durable("queue.login").build();
    }

    @Bean
    Binding bindLogin(Queue loginQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(loginQueue).to(userEventsExchange).with("login");
    }

    @Bean
    Queue passwordRecoveryQueue() {
        return QueueBuilder.durable("queue.password-recovery").build();
    }

    @Bean
    Binding bindPasswordRecovery(Queue passwordRecoveryQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(passwordRecoveryQueue).to(userEventsExchange).with("password-recovery");
    }

    @Bean
    Queue passwordUpdateQueue() {
        return QueueBuilder.durable("queue.password-update").build();
    }

    @Bean
    Binding bindPasswordUpdate(Queue passwordUpdateQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(passwordUpdateQueue).to(userEventsExchange).with("password-update");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}