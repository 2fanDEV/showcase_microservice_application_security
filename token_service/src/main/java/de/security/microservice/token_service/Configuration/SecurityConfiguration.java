package de.security.microservice.token_service.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Filter Chain
 * that does say that we allow every request
 * since this is not a service where we need to look up if a user has a token
 * for authorization purposes because we get the tokens from this service
 */
@EnableWebSecurity
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
	throws Exception {
		httpSecurity.csrf().disable().cors().and().authorizeHttpRequests().anyRequest().permitAll();
		return httpSecurity.build();
	}

}
