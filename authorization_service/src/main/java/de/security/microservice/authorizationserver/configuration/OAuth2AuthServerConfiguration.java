package de.security.microservice.authorizationserver.configuration;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import de.security.microservice.authorizationserver.model.MyUser;
import de.security.microservice.authorizationserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * this is the Oauth2 server configuration class
 * Documentation: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html
 * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/configuration-model.html
 * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/getting-started.html#developing-your-first-application
 * https://youtu.be/ZIjqDIdFyBw?t=493 <- getting started with spring authorization server due to
 * replacing the InMemoryOAuthservice with the JdbcTemplate
 * as well as the comments in the code below
 */
@Configuration(proxyBeanMethods = true)
public class OAuth2AuthServerConfiguration {


    @Autowired
    UserRepository userRepository;

    /**
     * Using the default Security OAuth2 applies where it
     * tries to match the endpoints that are available for requesting a token
     * Ref:
     * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/configuration-model.html#default-configuration
     * https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity httpSecurity)
    throws Exception
    {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);

        httpSecurity.cors().and().
                formLogin(Customizer.withDefaults()).requiresChannel().anyRequest().requiresSecure();

        return httpSecurity.build();
    }



    /**
     *  RegisteredClientRepository is the repository where all the "clients" that interact with the users
     *  are able to register themselves or the owners of the "product" are able to insert a new client so we can
     *  access the authorization server to create authorization_codes
     *  Ref:
     *  (JDBC) https://github.com/spring-projects/spring-authorization-server/blob/main/samples/default-authorizationserver/src/main/java/sample/config/AuthorizationServerConfig.java
     *  https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/core-model-components.html#registered-client-repository
     *  https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/getting-started.html
     * @return {@link RegisteredClientRepository}
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
         JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

        RegisteredClient registeredClient = RegisteredClient.withId("frontend-client")
                .clientId("frontend-client")
                .clientName("frontend-client")
                .clientSecret(new BCryptPasswordEncoder().encode("1234"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("https://127.0.0.1/authorized")
                .redirectUri("https://host.docker.internal/authorized")
                .scope(OidcScopes.OPENID)
                .scope("anonymous")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(25L))
                        .reuseRefreshTokens(false)
                        .refreshTokenTimeToLive(Duration.ofMinutes(30L))
                        .build())
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        RegisteredClient registeredClient3 = RegisteredClient.withId("anonymous-client")
                        .clientId("anonymous-client")
                        .clientName("anonymous-client")
                        .clientSecret(new BCryptPasswordEncoder().encode("anonymous"))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                        .redirectUri("https://localhost/authorized")
                        .redirectUri("https://host.docker.internal/authorized")
                .scope("anonymous")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(60L))
                        .reuseRefreshTokens(false)
                        .build())
                        .build();

        jdbcRegisteredClientRepository.save(registeredClient);
        jdbcRegisteredClientRepository.save(registeredClient3);

        return jdbcRegisteredClientRepository;
    }

    /**
     * this is necessary to implement so we can delegate the token generation process to the accessTokenCustomizer() below
     * so we are actually modifying the token that is generated through the access_code
     * Ref:
     * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/core-model-components.html#oauth2-token-generator
     * @return {@link OAuth2TokenGenerator<?>}
     * @throws JOSEException
     */
    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator()
    throws JOSEException {
        JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(jwtTokenCustomizer());
        OAuth2AccessTokenGenerator oAuth2AccessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(jwtGenerator, oAuth2AccessTokenGenerator, refreshTokenGenerator);
    }

    /**
     * I want to customize the tokens the auth server issues
     * so we can add "Roles" or "Claims" to it and the documentation
     * of spring said that this is the way to do so for jwt bearer tokens
     * in this case we are adding  the claims Role for Anonymous Token and Authorized Tokens
     * Ref:
     * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/core-model-components.html#oauth2-token-customizer
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return jwtEncodingContext -> {
            JwsHeader.Builder headers = jwtEncodingContext.getHeaders();
            JwtClaimsSet.Builder claims = jwtEncodingContext.getClaims();
            if(jwtEncodingContext.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN))
            {
               MyUser myUser = userRepository.findUserByUsername(jwtEncodingContext.getPrincipal().getName());
                Set<String> grantedAuthorities = new HashSet<>();
               if(myUser != null) {
                   myUser.getAuthorities().forEach((authority) -> {
                      grantedAuthorities.add(authority.getGrantedAuthority());
                   });
                   claims.claim("role", grantedAuthorities);
               } else {
                   /**
                    * we are adding a UUID to this anonymous token so the keyresolver in the api-gateway is still working
                    * as intended
                    */
                   claims.claim("sub", "anonymous-client-" + UUID.randomUUID().toString());
                   claims.claim("role", "ANON");

               }
           }
        };
    }

    /**
     * this is just so we can have the authorization tokens in a database instead of the memory of the server
     *
     * This is the official repository of spring, which includes sample applications to show how functions are to be implemented
     * https://github.com/spring-projects/spring-authorization-server/blob/main/samples/default-authorizationserver/src/main/java/sample/config/AuthorizationServerConfig.java
     *
     * Finding out that a JDBC implementation was possible, was given by the documentation
     * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/core-model-components.html#oauth2-authorization-service
     *  @param jdbcTemplate
     * @param registeredClientRepository
     * @return
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository)
    {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * this is just so we can have the authorization tokens in a database instead of the memory of the server
     *
     * This is the official repository of spring, which includes sample applications to show how functions are to be implemented
     * https://github.com/spring-projects/spring-authorization-server/blob/main/samples/default-authorizationserver/src/main/java/sample/config/AuthorizationServerConfig.java
     *
     * Finding out that a JDBC implementation was possible, was given by the documentation
     * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/core-model-components.html#oauth2-authorization-consent-service
     * @param jdbcTemplate
     * @param registeredClientRepository
     * @return
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository)
    {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }


    /**
     * Generating an RSA public/priv keypair such that we
     * can insert it in {@link JWKSource} function below
     * which configures the JWK Endpoint
     *
     * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/getting-started.html#developing-your-first-application
     * @return
     * @throws JOSEException
     */
    public KeyPair genRSAKey()
    throws JOSEException
    {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch(Exception e)
        {
            throw new IllegalStateException(e);
        }

        return keyPair;
    }

    /**
     * this is for the jwk endpoint where every server is accessing the {@link JWKSet} Endpoint that is created with this function
     * the JWK Endpoint is accessible at localhost:8977/oauth2/authorize
     *
     * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/getting-started.html#developing-your-first-application
     * @return
     * @throws JOSEException
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource()
    throws JOSEException
    {
        KeyPair keyPair = genRSAKey();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey)
                                    .privateKey(rsaPrivateKey)
                                    .keyID(UUID.randomUUID().toString())
                                    .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * This was mandatory at the point of development as otherwise the
     * authorization server would not start.
     *
     * The implementation now is given by
     * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/configuration-model.html#configuring-provider-settings
     * https://docs.spring.io/spring-authorization-server/docs/0.3.0/reference/html/getting-started.html#developing-your-first-application
     *
     * this is the providersettings that configure where the endpoints for
     * the oauth process with be placed.
     *
     * e.g if we say that "https:localhost:8977/authServer"
     * than all the endpoints for the oauth process will be appended to that string
     * @return {@link ProviderSettings}
     */
    @Bean
    public ProviderSettings providerSettings()
    {
        return ProviderSettings.builder().build();
    }

}
