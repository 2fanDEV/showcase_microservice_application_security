package de.security.microservice.api_gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import io.github.resilience4j.timelimiter.TimeLimiterConfig;


import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 *  Configuration class for the CircuitBreaker
 */

@Configuration
public class CircuitBreakerConfiguration {

    /**
     * Started out with this video:
     * https://youtu.be/5eAQLygfmbg?t=1249
     * but afterwards I found the documentation
     * on the Spring Boot Documentation page
     * and changed the circuit breaker customization
     *
     * this is the customizer for WebFlux Application
     * where we set the timeout duration when the failed percentage of
     * requests is above the failureRateThreshhold (45%) and the circuit is open
     * and after the timeout period the circuit closes again and accepts requests
     * Reference: https://docs.spring.io/spring-cloud-circuitbreaker/docs/current/reference/html/#reactive-example-2
     * @return
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer()
    {

        return factory -> factory.configure(builder -> {
            builder.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(5)).build())
                    .circuitBreakerConfig(CircuitBreakerConfig.custom().failureRateThreshold(45).build()).build();
        });

    }

}
