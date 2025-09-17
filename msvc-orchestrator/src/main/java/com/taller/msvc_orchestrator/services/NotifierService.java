package com.taller.msvc_orchestrator.services;

public interface NotifierService {
    void sendNotification(String chanel, String destination, String message);
}
