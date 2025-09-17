package com.taller.msvc_security.http;

import com.taller.msvc_security.Models.NotificationCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "orchestrator-service",
        url = "${LB_ORCHESTRATOR_URL:http://127.0.0.1:8083}"
)
public interface HttpOrchestratorClient {
    @PostMapping("api/notifications")
    void createAndSend(@RequestBody NotificationCreateRequest request);
}
