package com.example.neo_bank.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NeobankApiApplication {

	public static void main(String[] args) {
        SpringApplication.run(NeobankApiApplication.class, args);
	}

}
