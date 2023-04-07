package de.security.microservice.api_gateway.config;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Objects;


/**
 * A configuration class for a the KeyResolver that we are inserting into the
 * ratelimiter
 *
 * This video helped me in implementing the RateLimiter which the Redis Database.
 * https://youtu.be/5eAQLygfmbg?t=1785
 */
@Configuration
public class RateLimiterKeyResolver implements KeyResolver {

    /**
     * Taking the username out of the JwtBearer token and using that
     * as a name for the container in the redis db
     * and throwing an exception when the jwt is not parseable
     * @param exchange
     * @return {@link Mono<String>}
     */
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {

        Logger logger = LoggerFactory.getLogger(RateLimiterKeyResolver.class);
        String jwtBearer = Objects.requireNonNull(exchange.getRequest().getHeaders().get("Authorization")).get(0).replace("Bearer ", "");
        logger.info("AUTH HEADER: {}", jwtBearer);
        try {
            JWT jwt = JWTParser.parse(jwtBearer);
            String username =  jwt.getJWTClaimsSet().getStringClaim("sub");
            logger.info("username: {}", username);
            return Mono.just(username);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}


