package de.security.microservice.userservice.configuration.Security;

import de.security.microservice.userservice.configuration.Roles.Roles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * this is the security configuration
 * for the user service
 * It has a bunch of paths defined which are only authorized to be called a specific
 * entity who has the right roles
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final static String USER_SERVICE_BASE_PATH = "/api/services/user-service";

    private final static String REGISTER_USER = "/registerUser";
    private final static String GET_USER = "/getUser";
    private final static String DOES_USER_EXIST = "/doesUserExist";

    /**
     * the SecurityFilterChain where we set which requests
     * are to set that his server is simultaneously a resource server
     * but also what to look out for in the JWT token since we're setting a {@link JwtAuthenticationConverter}
     * creating a login form, a logout endpoint and delete all cookies when we log out
     *      * https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable().cors().and().authorizeRequests((auth) -> {
            auth.antMatchers(USER_SERVICE_BASE_PATH + GET_USER).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                .antMatchers(USER_SERVICE_BASE_PATH + REGISTER_USER).permitAll()
                .antMatchers(USER_SERVICE_BASE_PATH + DOES_USER_EXIST).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                .anyRequest().hasAnyAuthority(Roles.ADMIN);
        }).oauth2ResourceServer((oauth2) -> oauth2
                .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter()));

        return http.build();
    }

    /**
     * this is the jwtAuthenticatoinConverter where we are changing tht we are not
     * going to look for "scope_" property but rather the "ROLE_" property inside
     * the jwt token
     * https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
     * @return
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        JwtGrantedAuthoritiesConverter conv = new JwtGrantedAuthoritiesConverter();
        conv.setAuthorityPrefix("ROLE_");
        conv.setAuthoritiesClaimName("role");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(conv);

        return jwtAuthenticationConverter;
    }

}
