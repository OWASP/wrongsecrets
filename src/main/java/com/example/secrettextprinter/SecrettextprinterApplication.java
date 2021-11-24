package com.example.secrettextprinter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@SpringBootApplication
public class SecrettextprinterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecrettextprinterApplication.class, args);
	}

	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public InMemoryScoreCard scoreCard() {
		return new InMemoryScoreCard(11);
	}

}
