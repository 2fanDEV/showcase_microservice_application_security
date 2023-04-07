package de.security.microservice.commentservice.Configuration;

import de.security.microservice.commentservice.feign.blogServiceFeign.configuration.Roles.Roles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;


/**
 * this is the security configuration
 * for the blog service
 * It has a bunch of paths defined which are only authorized to be called a specific
 * entity who has the right roles
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    public static final String BASE_API_COMMENT_SERVICE = "/api/services/comment-service";
    public static final String COMMENT_SERVICE_CREATE = "/createComment";
    public static final String COMMENT_SERVICE_EDIT = "/editComment";
    public static final String COMMENT_SERVICE_DELETE= "/deleteComment";
    public static final String COMMENT_SERVICE_AMOUNT_COMMENTS = "/getCommentAmountForBlogId/**";

    public static final String GET_COMMENT_BY_BLOG_ID = "/getCommentsByBlogId/**";

    public static final String GET_COMMENT_SIZE_BY_BLOG_ID = "/getCommentSizeByBlogId";

    public static final String GET_COMMENT_SIZE_BY_USER_NAME = "/getCommentsSizeByUserName";

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
            auth.antMatchers(BASE_API_COMMENT_SERVICE + COMMENT_SERVICE_CREATE).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                    .antMatchers(BASE_API_COMMENT_SERVICE + COMMENT_SERVICE_EDIT).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                    .antMatchers(BASE_API_COMMENT_SERVICE + COMMENT_SERVICE_DELETE).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                    .antMatchers(BASE_API_COMMENT_SERVICE + COMMENT_SERVICE_DELETE).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                    .antMatchers(BASE_API_COMMENT_SERVICE + COMMENT_SERVICE_AMOUNT_COMMENTS).hasAnyAuthority(Roles.ANONYMOUS, Roles.USER, Roles.ADMIN)
                    .antMatchers( BASE_API_COMMENT_SERVICE + GET_COMMENT_BY_BLOG_ID).hasAnyAuthority(Roles.ANONYMOUS, Roles.USER, Roles.ADMIN)
                    .antMatchers(BASE_API_COMMENT_SERVICE + GET_COMMENT_SIZE_BY_BLOG_ID).hasAnyAuthority(Roles.ANONYMOUS, Roles.USER, Roles.ADMIN)
                    .antMatchers(BASE_API_COMMENT_SERVICE + GET_COMMENT_SIZE_BY_USER_NAME).hasAnyAuthority(Roles.ANONYMOUS, Roles.USER, Roles.ADMIN)
                    .anyRequest().permitAll();
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
