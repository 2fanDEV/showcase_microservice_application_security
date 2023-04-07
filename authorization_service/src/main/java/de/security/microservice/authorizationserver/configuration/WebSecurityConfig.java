package de.security.microservice.authorizationserver.configuration;

import de.security.microservice.authorizationserver.repositories.UserRepository;
import de.security.microservice.authorizationserver.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * WebSecurityConfig.class
 */
@EnableWebSecurity
public class WebSecurityConfig {
    private final static String BASE_AUTH_API_URI = "/api/services/auth-service";
    private final static String GET_USER = "/getUser";
    private final UserRepository userRepository;
    private final MyUserDetailsService myUserDetailsService;

    /**
     * Constructor with two autowired parameters
     * @param myUserDetailsService
     * @param userRepository
     */
    @Autowired
    public WebSecurityConfig(MyUserDetailsService myUserDetailsService, UserRepository userRepository)
    {
        this.myUserDetailsService = myUserDetailsService;
        this.userRepository = userRepository;
    }


    /**
     * the SecurityFilterChain where we set which requests
     * are to set that his server is simultaneously a resource server
     * but also what to look out for in the JWT token since we're setting a {@link JwtAuthenticationConverter}
     * creating a login form, a logout endpoint and delete all cookies when we log out
     * https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
     * https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
    throws Exception
    {
        httpSecurity.cors().and()
                .authorizeRequests((auth) -> {
                        auth.antMatchers("/register").permitAll()
                                .antMatchers(("/registerUser")).permitAll()
                                .antMatchers(BASE_AUTH_API_URI + GET_USER).hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                                .anyRequest().permitAll();
        }).oauth2ResourceServer((oauth) ->
            oauth.jwt().jwtAuthenticationConverter(jwtAuthenticationConverter()))
            .formLogin(thymeleafTemplate ->
                        thymeleafTemplate.loginPage("/login").
                        permitAll())
                .logout()
                .logoutUrl("/logout")
                .permitAll()
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "access_token", "refresh_token");
        return httpSecurity.build();
    }

    /**
     * this is the jwtAuthenticatoinConverter where we are changing tht we are not
     * going to look for "scope_" property but rather the "ROLE_" property inside
     * the jwt token
     * https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-authorization-extraction
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

    /**
     * https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html#servlet-authentication-daoauthenticationprovider
     * we are saying that we are using a custom userdetailservice as the authentication provider
     * than the already integrated userdetailservice.
     *
     * this is necessary if we want to create new columns in the db scheme for such as
     * if the user is getting locked
     *
     * I read first about the DaoAuthenticationProvider in this tutorial
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * and then looked into the link at the top of this comment
     * to see how this works and tried to set the UserDetailService
     * that we created with the help of that tutorial
     *
     * However, a passwordencoder was needed to set a bean of it, as
     * it overrides the standard configuration of the DaoAuthenticationProvider
     * and the only passwordencoder that is included in Spring
     * that I know of was the BCryptPasswordEncoder which we eventually used
     *
     * @return {@link DaoAuthenticationProvider}
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(myUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }


    /**
     * Setting the spring default password encoder so the pw is not stored in plaintext in the database
     * https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-bcrypt
     * https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/password-encoder.html
     * @return {@link BCryptPasswordEncoder}
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

}
