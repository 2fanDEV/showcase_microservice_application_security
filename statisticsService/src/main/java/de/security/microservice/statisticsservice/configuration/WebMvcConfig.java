package de.security.microservice.statisticsservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * If creating a custom interceptor for requests
 * a configuration is needed that adds the interceptor
 * to the server.
 *
 * https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-config-interceptors
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(createNewInstanceCustomInterceptor());
    }

    @Bean
    public CustomRequestInterceptor createNewInstanceCustomInterceptor()
    {
        return new CustomRequestInterceptor();
    }


}
