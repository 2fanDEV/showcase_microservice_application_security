package de.security.microservice.blogservice.Config;

import de.security.microservice.blogservice.Config.Roles.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    public static final String BASE_API_BLOG_SERVICE = "/api/services/blog-service";
    public static final String CREATE_BLOGS = "/createBlog";
    public static final String GET_ALL_BLOG_ARTICLES = "/getAllBlogArticles";

    public static final String GET_ALL_BLOGS_BY_ID = "/getAllBlogArticlesById";
    public static final String GET_ALL_BLOGS_LIKED_BY_USERNAME = "/getAllBlogArticlesLikedByUserName";

    public static final String GET_ALL_BLOGS_BY_USERNAME = "/getAllBlogArticlesByUserName";

    public static final String GET_ALL_BLOGS_BY_USERNAME2 = "/getBlogsByUserName";
    public static final String GET_ALL_BLOGS_USER_COMMENTED_ON = "/getAllBlogsUserCommentedOn";
    public static final String DELETE_BLOG_ARTICLE = "/deleteBlogArticle";

    /**
     * the SecurityFilterChain where we set which requests
     * are to set that his server is simultaneously a resource server
     * but also what to look out for in the JWT token since we're setting a {@link JwtAuthenticationConverter}
     * creating a login form, a logout endpoint and delete all cookies when we log out
     * https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-sansboot
     * @param httpSecurity
     * @return {@link SecurityFilterChain}
     * @throws Exception
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and().authorizeRequests((auth) -> {
            auth.antMatchers(HttpMethod.POST, BASE_API_BLOG_SERVICE + CREATE_BLOGS).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                    .antMatchers(HttpMethod.GET,BASE_API_BLOG_SERVICE + GET_ALL_BLOG_ARTICLES).hasAnyAuthority(Roles.ANONYMOUS, Roles.USER, Roles.ADMIN)
                    .antMatchers(HttpMethod.GET,BASE_API_BLOG_SERVICE + GET_ALL_BLOGS_LIKED_BY_USERNAME).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                    .antMatchers(HttpMethod.GET,BASE_API_BLOG_SERVICE +  GET_ALL_BLOGS_BY_USERNAME).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                    .antMatchers(HttpMethod.GET,BASE_API_BLOG_SERVICE + GET_ALL_BLOGS_USER_COMMENTED_ON).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                    .antMatchers(HttpMethod.POST,BASE_API_BLOG_SERVICE + DELETE_BLOG_ARTICLE).hasAnyAuthority(Roles.USER, Roles.ADMIN)
                    .antMatchers(HttpMethod.POST, BASE_API_BLOG_SERVICE + GET_ALL_BLOGS_BY_USERNAME2).hasAnyAuthority(Roles.ANONYMOUS, Roles.USER, Roles.ADMIN)
                    .antMatchers(HttpMethod.POST, BASE_API_BLOG_SERVICE + GET_ALL_BLOGS_BY_ID).hasAnyAuthority(Roles.ANONYMOUS, Roles.USER, Roles.ADMIN)
                    .anyRequest().hasAnyAuthority(Roles.ANONYMOUS, Roles.USER, Roles.ADMIN);
        }).oauth2ResourceServer((oauth2) -> oauth2
                .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter()));
        return http.build();
    }

    /**
     * this is the jwtAuthenticatoinConverter where we are changing tht we are not
     * going to look for "scope_" property but rather the "ROLE_" property inside
     * the jwt token
     * https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-authorization-extraction
     * @return {@link JwtAuthenticationConverter}
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
