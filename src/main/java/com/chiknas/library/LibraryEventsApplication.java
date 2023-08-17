package com.chiknas.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LibraryEventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryEventsApplication.class, args);
	}

}
