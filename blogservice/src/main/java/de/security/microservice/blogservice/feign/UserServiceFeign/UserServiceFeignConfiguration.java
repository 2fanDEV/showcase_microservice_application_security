package de.security.microservice.blogservice.feign.UserServiceFeign;

import de.security.microservice.blogservice.Config.FeignHelperService;
import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;


/**
 * this is a configuration class to intercept the outgoing
 * request and insert the authorization token to it
 *
 *   https://cloud.spring.io/spring-cloud-openfeign/reference/html/
 */
public class UserServiceFeignConfiguration {

    Logger logger = LoggerFactory.getLogger(UserServiceFeignConfiguration.class);

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
