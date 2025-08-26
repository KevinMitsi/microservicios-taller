package com.taller.msvc_consumer.service.impl;

import com.taller.msvc_consumer.service.RandomStringService;
import org.springframework.stereotype.Service;

import java.util.Random;


@Service
public class RandomStringServiceImpl implements RandomStringService {
    private final Random random = new Random();
    @Override
    public String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int rValue = this.random.nextInt(chars.length());

        return String.valueOf(chars.charAt(rValue)).repeat(Math.max(0, length)); // Convierte el StringBuilder a una cadena y lo devuelve

    }
}
