package de.security.microservice.api_gateway.routing.Controller.CircuitBreaker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * This was implemented with the documentation having
 * a .setFallBackURI function at the circuitbreaker in the RouteLocator
 * function
 *
 * Additionally, I saw this tutorial on implementing the RateLimiter
 * where the developer did also implement the fallback methods
 * so this also counts as the reference to implement fallBackUris
 *
 * Ref: https://docs.spring.io/spring-cloud-gateway/docs/4.0.0/reference/html/#spring-cloud-circuitbreaker-filter-factory
 * https://youtu.be/5eAQLygfmbg?t=1249
 */
@RestController
@RequestMapping("/fallBack")
public class FallBackController {


    /**
     * This is the fallback method for if the circuit breaker opens the connection
     * to the commentService
     */
    @RequestMapping(value = "/commentService")
    public ResponseEntity<Mono<String>>  fallBackCommentService() {
        return ResponseEntity.status(408).body(Mono.just("Sorry, something went wrong trying to interact with the CommentService"));
    }

    /**
     * This is the fallback method for if the circuit breaker opens the connection
     * to the statisticsService
     * @return
     */
    @RequestMapping(value = "/statisticService")
    public ResponseEntity<Mono<String>>  fallBackStatisticService()
    {
        return ResponseEntity.status(408).body(Mono.just("Sorry, something went wrong trying to interact with the StatisticService"));
    }

    /**
     * This is the fallback method for if the circuit breaker opens the connection
     * to the blogService
     */
    @RequestMapping(value = "/blogService")
    public ResponseEntity<Mono<String>>  fallBackBlogService()
    {
        return ResponseEntity.status(408).body(Mono.just("Sorry, something went wrong trying to interact with the BlogService"));
    }

    /**
     * This is the fallback method for if the circuit breaker opens the connection
     * to the userService
     */
    @RequestMapping(value = "userService")
    public ResponseEntity<Mono<String>>  fallBackUserService()
    {
        return ResponseEntity.status(408).body(Mono.just("Sorry, something went wrong trying to interact with the UserService"));
    }

    /**
     * This is the fallback method for if the circuit breaker opens the connection
     * to the authorization service
     * @return
     */
    @RequestMapping(value = "/authService")
    public ResponseEntity<Mono<String>> fallBackAuthService()
    {
        return ResponseEntity.status(408).body(Mono.just("Sorry, something went wrong trying to interact with the AuthService"));
    }

    /**
     * This is the fallback method for if the circuit breaker opens the connection
     * to the tokenService
     * @return
     */
    @RequestMapping(value = "/tokenService")
    public ResponseEntity<Mono<String>> fallBackTokenService()
    {
        return ResponseEntity.status(408).body(Mono.just("Sorry, something went wrong trying to interact with the TokenService"));
    }

}
