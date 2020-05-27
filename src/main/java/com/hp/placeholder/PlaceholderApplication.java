package com.hp.placeholder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.hp.placeholder"
})
public class PlaceholderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaceholderApplication.class, args);
	}

}
