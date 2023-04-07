package de.security.microservice.statisticsservice.feign.config;

import de.security.microservice.statisticsservice.configuration.FeignHelperService.FeignHelperService;
import de.security.microservice.statisticsservice.services.ServletService.ServletService;
import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;

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
@Configuration
public class GeneralFeignConfiguration {

    Logger logger = LoggerFactory.getLogger(GeneralFeignConfiguration.class);

    @Autowired
    FeignHelperService feignTokenRelayHelper;
    @Autowired
    ServletService servletService;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            try {
                logger.info("Current USER IN TOKEN: {}", servletService.getUserNameFromToken(this.feignTokenRelayHelper.getAuthorizationHeader().replace("Bearer", "")));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            requestTemplate.header("Authorization", this.feignTokenRelayHelper.getAuthorizationHeader());
        };
    }

    @Bean
    feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.BASIC;
    }
}
