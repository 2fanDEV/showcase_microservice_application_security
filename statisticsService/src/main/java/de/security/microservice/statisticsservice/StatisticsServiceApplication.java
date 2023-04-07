package de.security.microservice.statisticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Entrypoint for the API Gateway
 * @EnableFeignClients enables the functionality of feign
 * @EnableJpaRepositories looks for the repository files
 * https://cloud.spring.io/spring-cloud-openfeign/reference/html/
 * https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/config/EnableJpaRepositories.html
 */
@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories
public class StatisticsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatisticsServiceApplication.class, args);
	}

}
