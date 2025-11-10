package com.taller.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "integration.test")
@Data
public class IntegrationTestConfig {

    private String securityBaseUrl = "http://localhost:8080";
    private String saludoBaseUrl = "http://localhost:80";
    private String consumerBaseUrl = "http://localhost:8081";
    private String orchestratorBaseUrl = "http://localhost:8083";
    private String deliveryBaseUrl = "http://localhost:8082";
    private String monitoringBaseUrl = "http://localhost:8000";

    private int maxRetries = 3;
    private int retryDelayMs = 1000;
    private int asyncTimeoutSeconds = 30;
}

