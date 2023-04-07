package de.security.microservice.userservice.feign;

import de.security.microservice.userservice.configuration.Service.FeignHelperService;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * this is a configuration class to intercept the outgoing
 * request and insert the authorization token to it
 *
 *  https://cloud.spring.io/spring-cloud-openfeign/reference/html/
 *
 *  shows that there can be a {@link RequestInterceptor}
 *  included which then intercepts the outgoing
 *  request before relaying it further.
 */
public class AuthServerFeignConfiguration {

    Logger logger = LoggerFactory.getLogger(AuthServerFeignConfiguration.class);

    @Autowired
    FeignHelperService feignHelperService;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", this.feignHelperService.getAuthorizationHeader());
        };
    }

    @Bean
    feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.BASIC;
    }

}
