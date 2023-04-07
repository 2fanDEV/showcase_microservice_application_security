package de.security.microservice.api_gateway.routing;

import de.security.microservice.api_gateway.config.RateLimitConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.RewriteLocationResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * This is the general Router class that redirects all requests to the according
 * services.
 *
 * There are two possible ways to implement this.
 * First of all, there is the way through the application.yml//application.properties file
 * where you are able to configure all of this as well.
 *
 * The second option is the one presented to you in this class in
 * a java programmed way
 *
 * Reference:
 * https://docs.spring.io/spring-cloud-gateway/docs/4.0.0/reference/html/#spring-cloud-circuitbreaker-filter-factory
 * https://docs.spring.io/spring-cloud-gateway/docs/4.0.0/reference/html/#circuit-breaker-status-codes
 * https://docs.spring.io/spring-cloud-gateway/docs/4.0.0/reference/html/#the-rewritelocationresponseheader-gatewayfilter-factory
 *
 * The example 24. and 28. helped me in implementing the api gateway in the way it is currently implemented in Java.
 * Additionally, the rewritelocationresponseheader with the protocoltypes and the never strip function
 * was implemented with the third link
 */
@Component
public class Router implements RouterInterface {

    private final String authServerURI;
    private final String tokenServiceURI;
    private final String userServiceURI;
    private final String blogServiceURI;
    private final String commentServiceURI;
    private final String statisticServiceURI;
    private final RateLimitConfig rateLimitConfig;


    /**
     * This is the constructor where we are taking all the URIs that
     * we appointed in the application.properties file and store them in separate Strings
     * @param rateLimitConfig
     */
    @Autowired
    Router(RateLimitConfig rateLimitConfig) {
        this.authServerURI = "lb://AUTHORIZATION-SERVICE";
        this.tokenServiceURI = "lb://TOKEN-SERVICE";
        this.userServiceURI = "lb://USER-SERVICE";
        this.blogServiceURI = "lb://BLOG-SERVICE";
        this.commentServiceURI = "lb://COMMENT-SERVICE";
        this.statisticServiceURI = "lb://STATISTICS-SERVICE";
        this.rateLimitConfig = rateLimitConfig;
    }


    /**
     * This is the Router that does redirect the specific requests
     * to the according services
     *
     * For example we are accepting a request that does have the
     * path "/api/v0/comment-service". With a simple regular expression
     * we are removing the v0 out of the path and replace it with "services"
     * since all the services have endpoints that have a path prefix that
     * start with /api/services instead of v0.
     *
     * Also this is the place where we are adding the RateLimiter and CircuitBreaker
     * for all the routes
     *
     * Reference for the ratelimiter: https://www.youtube.com/watch?v=5eAQLygfmbg
     *
     * @param builder
     * @return {@link RouteLocator}
     */
    @Bean
    @Override
    public RouteLocator routes(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder b = builder.routes()
                .route((currentRoute) -> currentRoute.path("/api/v0/token-service/**")
                        .filters(filter -> filter.rewritePath("^\\/api\\/v0\\/", "/api/services/")
                                .requestRateLimiter((rateLimiter) -> rateLimiter.setRateLimiter(rateLimitConfig.rateLimiter())
                                .setKeyResolver(rateLimitConfig.keyResolverForNoToken()))
                                .circuitBreaker(circuitBreaker -> circuitBreaker.setName("tokenService").setFallbackUri("/fallBack/tokenService")))
                        .uri(tokenServiceURI))
                .route((currentRoute) -> currentRoute.path("/api/v0/comment-service/**")
                        .filters(filter -> filter.rewritePath("^\\/api\\/v0\\/", "/api/services/")
                                .requestRateLimiter((rateLimiter) -> rateLimiter.setRateLimiter(rateLimitConfig.rateLimiter()))
                                .circuitBreaker((circuitBreaker) -> circuitBreaker.setName("commentService").setFallbackUri("/fallBack/commentService")))
                        .uri(commentServiceURI))
                .route((currentRoute) -> currentRoute.path("/api/v0/statistics-service/**")
                        .filters(filter -> filter.rewritePath("^\\/api\\/v0\\/", "/api/services/")
                                .requestRateLimiter((rateLimiter) -> rateLimiter.setRateLimiter(rateLimitConfig.rateLimiter()))
                                .circuitBreaker((circuitBreaker) -> circuitBreaker.setName("statistics-service").setFallbackUri("/fallBack/statisticService")))
                        .uri(statisticServiceURI))
                .route((currentRoute) -> currentRoute.path("/api/v0/user-service/**")
                        .filters(filter ->  filter.rewritePath("^\\/api\\/v0\\/", "/api/services/")
                                .requestRateLimiter().configure(
                                            (rateLimiter) -> rateLimiter.setRateLimiter(rateLimitConfig.rateLimiter()))
                                .circuitBreaker((circuitBreaker) -> circuitBreaker.setName("userServiceCB").setFallbackUri("/fallBack/userService")))
                        .uri(userServiceURI))
                .route((currentRoute) -> currentRoute.path("/api/v0/blog-service/**")
                        .filters(filter -> filter.rewritePath("^\\/api\\/v0\\/", "/api/services/")
                                .requestRateLimiter((rateLimiter) -> rateLimiter.setRateLimiter(rateLimitConfig.rateLimiter()))
                                .circuitBreaker((circuitBreaker) -> circuitBreaker.setName("blogServiceCB").setFallbackUri("/fallBack/blogService")))
                        .uri(blogServiceURI))
                .route((currentRoute) -> currentRoute.path("/api/v0/auth-service/login")
                        .filters(filter -> filter.rewritePath("^\\/api\\/v0\\/auth-service\\/", "/")
                                .requestRateLimiter((rateLimiter) -> rateLimiter.setRateLimiter(rateLimitConfig.rateLimiter())
                                        .setKeyResolver(rateLimitConfig.keyResolverForNoToken()))
                                .circuitBreaker((circuitBreaker) -> circuitBreaker.setFallbackUri("/fallBack/authService"))
                                .rewriteLocationResponseHeader(String.valueOf(RewriteLocationResponseHeaderGatewayFilterFactory.StripVersion.NEVER_STRIP),
                                        "Location",
                                        "localhost/api/v0/auth-service",
                                        "http|https|ftp|ftps")
                        )
                        .uri(authServerURI))
                .route((currentRoute) -> currentRoute.path("/api/v0/auth-service/oauth2/authorize")
                        .filters(filter -> filter.rewritePath("^\\/api\\/v0\\/auth-service\\/", "/")
                                .requestRateLimiter((rateLimiter) -> rateLimiter.setRateLimiter(rateLimitConfig.rateLimiter())
                                        .setKeyResolver(rateLimitConfig.keyResolverForNoToken()))
                                .circuitBreaker((circuitBreaker) -> circuitBreaker.setName("authService2").setFallbackUri("/fallBack/authService"))
                                .rewriteLocationResponseHeader(String.valueOf(RewriteLocationResponseHeaderGatewayFilterFactory.StripVersion.NEVER_STRIP),
                                        "Location",
                                        "localhost/api/v0/auth-service",
                                        "http|https|ftp|ftps")
                        )
                        .uri(authServerURI))
                .route(((currentRoute) -> currentRoute.path("/api/v0/auth-service/**")
                        .filters(filter -> filter.rewritePath("^\\/api\\/v0\\/auth-service\\/", "/")
                                .requestRateLimiter((rateLimiter) ->
                                        rateLimiter.setRateLimiter(rateLimitConfig.rateLimiter()).setKeyResolver(rateLimitConfig.keyResolverForNoToken()))
                                        .circuitBreaker((circuitBreaker) -> circuitBreaker.setName("authService3").setFallbackUri("/fallBack/authService"))
                        )
                        .uri(authServerURI)
                ));
        return b.build();
    }


}
