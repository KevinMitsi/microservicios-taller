package com.taller.msvc_orchestrator.service;

public interface NotifierService {
    void sendNotification(String chanel, String destination, String message);
}
