package com.taller.msvc_saludo.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingsController {

    @GetMapping("/saludo")
    public ResponseEntity<String> greet(@RequestParam String nombre) {
        return ResponseEntity.ok("Hola " + nombre + ", bienvenido al microservicio de saludo!");
    }

    @GetMapping("/saludo/test")
    public ResponseEntity<String> testGreet(@RequestParam(defaultValue = "Test") String nombre) {
        return ResponseEntity.ok("Hola " + nombre + ", este es un saludo de prueba!");
    }
}
