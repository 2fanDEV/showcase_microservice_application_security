package de.security.microservice.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Entrypoint for the API Gateway
 * @EnableFeignClients enables the functionality of feign
 * @EnableJpaRepositories specifies where the files for the repositories are located
 * https://cloud.spring.io/spring-cloud-openfeign/reference/html/
 * https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/config/EnableJpaRepositories.html
 */
@SpringBootApplication
@EnableFeignClients
@EnableJpaRepositories("de.security.microservice.userservice.repositories")
public class UserserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserserviceApplication.class, args);
    }

}
