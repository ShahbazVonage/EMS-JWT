package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmsJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmsJwtApplication.class, args);
		System.out.println("Server is up");
	}

}
