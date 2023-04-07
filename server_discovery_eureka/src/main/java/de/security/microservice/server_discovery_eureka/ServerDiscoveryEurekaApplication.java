package de.security.microservice.server_discovery_eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Entry Point for
 * Spring Boot Application
 *
 * @EnableEurekaServer enables the discovery service
 * and starts the server defined in the application.yml
 *
 * https://spring.io/guides/gs/service-registration-and-discovery/
 */

@CrossOrigin
@SpringBootApplication
@EnableEurekaServer
public class ServerDiscoveryEurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerDiscoveryEurekaApplication.class, args);
    }

}
