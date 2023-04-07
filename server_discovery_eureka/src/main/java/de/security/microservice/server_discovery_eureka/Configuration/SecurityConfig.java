package de.security.microservice.server_discovery_eureka.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    /**
     * A securityFilterChain for disabling csrf token
     * since the eureka server currently is only being able to
     * secure with HTTPBasic and due to that we have to disable the
     * csrf. Otherwise the services that need to register won't be able to
     * register with the eureka server since they don't generate csrf tokens
     *
     * https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/#authenticating-with-the-eureka-server
     * https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/#securing-the-eureka-server
     *
     * @param httpSecurity
     * @return {@link SecurityFilterChain}
     * @throws Exception
     */
    @Bean
    SecurityFilterChain securityFilterchain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf().disable().httpBasic(Customizer.withDefaults())
                .build();
    }

}
