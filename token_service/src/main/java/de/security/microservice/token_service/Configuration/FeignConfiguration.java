package de.security.microservice.token_service.Configuration;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 *
 * I just needed to see the logger level, to see if the
 * request did actually go to the correct location
 * and had the correct body params
 *
 * https://cloud.spring.io/spring-cloud-openfeign/reference/html/#feign-logging
 *
 */
public class FeignConfiguration {

	/*
	 * logging level setting
	 * @return
	 */
	@Bean
	Logger.Level feignLoggerLevel()
	{
		return Logger.Level.BASIC;
	}

}
