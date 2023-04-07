package de.security.microservice.api_gateway;

import de.security.microservice.api_gateway.routing.Router;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Import;


/**
 * Entrypoint for the API Gateway
 * @Import annotation is here to import the RouterClass which has the bean for the
 * RouteLocator defined inside of it as otherwise the routing did not work.
 */
@SpringBootApplication
@Import({Router.class})
public class ApiGatewayApplication {


	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
}
