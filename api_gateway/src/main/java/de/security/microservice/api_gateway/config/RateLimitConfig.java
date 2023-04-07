package de.security.microservice.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * RedisRateLimiter Configuration class
 *
 * There is not much documentation about the
 * RedisRateLimiter in a pure java way and only through the application.yml
 * and that did not work out well for me
 *
 * However, I wasn't able to find any documentation
 * on implementing it the way it was recommended in the Spring Docs
 *
 * This video helped me implement it in the way it is currently implemented
 * Video Source: https://youtu.be/5eAQLygfmbg?t=1785
 * https://spring.io/blog/2021/04/05/api-rate-limiting-with-spring-cloud-gateway
 */
@Configuration
public class RateLimitConfig {

    /**
     * Basically a bean that returns a RedisRateLimiter whenever
     * it is called.
     * @return {@link RedisRateLimiter}
     */
    @Bean
    public RedisRateLimiter rateLimiter()
    {
        return new RedisRateLimiter(4,8);
    }

    /**
     * This is a default keyResolver that is needed when there is no token available
     * and retrieved from RateLimiterKeyResolver.java
     * @return {@link KeyResolver}
     */
    public KeyResolver keyResolverForNoToken()
    {
        return exchange -> Mono.just("1");
    }

}
