package de.security.microservice.authorizationserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Entrypoint for the API Gateway
 * @EnableFeignClients enables the functionality of feign
 * @EnableJpaRepositories specifies where the files for the repositories are located
 * https://cloud.spring.io/spring-cloud-openfeign/reference/html/
 * https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/config/EnableJpaRepositories.html
 */
@SpringBootApplication
@EnableFeignClients
@EnableJdbcRepositories("de.security.microservice.authorizationserver.repositories")
public class AuthorizationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServiceApplication.class, args);
	}

}
