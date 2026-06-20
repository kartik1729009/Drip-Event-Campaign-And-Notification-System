package com.Kartik.notiflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotiflowApplication {

	public static void main(String[] args) {
		System.out.println("Starting the server");
		SpringApplication.run(NotiflowApplication.class, args);
	}

}
