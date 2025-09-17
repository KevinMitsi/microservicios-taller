package com.taller.msvc_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsvcSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcSecurityApplication.class, args);
	}

}
