package com.example.rollbasedlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RollbasedloginApplication {

	public static void main(String[] args) {
		SpringApplication.run(RollbasedloginApplication.class, args);
	}

}
