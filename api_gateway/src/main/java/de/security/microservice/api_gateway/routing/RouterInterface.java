package de.security.microservice.api_gateway.routing;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

/**
 * The interface for the Router
 */
public interface RouterInterface {

    public RouteLocator routes(RouteLocatorBuilder builder);

}
