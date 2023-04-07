package de.security.microservice.token_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entrypoint for the API Gateway
 * @EnableFeignClients enables the functionality of feign
 * https://cloud.spring.io/spring-cloud-openfeign/reference/html/
 */
@SpringBootApplication
@EnableFeignClients
public class TokenServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TokenServiceApplication.class, args);
	}

}
