package com.taller.msvc_consumer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final Instant startTime = Instant.now();

    @Value("${app.version:1.0.0}")
    private String version;

    @GetMapping
    public Map<String, Object> generalHealth() {
        return Map.of(
                "status", "UP",
                "version", version,
                "uptime", Duration.between(startTime, Instant.now()).toSeconds() + "s",
                "checks", new Object[]{
                        Map.of("name", "Readiness check", "status", "UP",
                                "data", Map.of("from", startTime.toString(), "status", "READY")),
                        Map.of("name", "Liveness check", "status", "UP",
                                "data", Map.of("from", startTime.toString(), "status", "ALIVE"))
                }
        );
    }

    @GetMapping("/ready")
    public Map<String, Object> readiness() {
        return Map.of(
                "status", "UP",
                "checks", new Object[]{
                        Map.of("name", "Readiness check", "status", "UP",
                                "data", Map.of("from", startTime.toString(), "status", "READY"))
                }
        );
    }

    @GetMapping("/live")
    public Map<String, Object> liveness() {
        return Map.of(
                "status", "UP",
                "checks", new Object[]{
                        Map.of("name", "Liveness check", "status", "UP",
                                "data", Map.of("from", startTime.toString(), "status", "ALIVE"))
                }
        );
    }
}

@RestController
class SmokeTestController {

    @GetMapping("/smoke")
    public Map<String, Object> smokeTest() {
        return Map.of(
                "status", "UP",
                "service", "msvc-consumer",
                "timestamp", System.currentTimeMillis(),
                "message", "Service is running"
        );
    }
}
