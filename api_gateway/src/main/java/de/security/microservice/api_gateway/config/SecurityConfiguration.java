package de.security.microservice.api_gateway.config;

import de.security.microservice.api_gateway.config.Roles.Roles;
import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    public String issuerUri;

    /**
     * Constructor of Class with the JWK Endpoint from the Authorization Server
     * to get the public-key from
     * @param issuerUri
     */
    @Autowired
    SecurityConfiguration(@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String issuerUri)
    {
        this.issuerUri = issuerUri;
    }

    /**
     * this is a different implementation of the filterchain since
     * Spring Cloud Gateway is a WebFlux Application
     * For instance we are using a SecurityWebFilterChain instead of SecurityFilterChain
     * and we are also grabbing the public key not when the server is starting but more
     * or less sending a request to get it when the first request hits the gateway
     *
     * Basically we are saying that except for the token-service
     * and for the auth-service, there is no need to authorize as
     * a user does not have a token before contacting both of these
     * services.
     *
     * To contact any other service however, that entity needs a token, which
     * needs to have one of three respective roles: [Roles.ADMIN, Roles.USER, Roles.ANONYMOUS]
     *
     * https://docs.spring.io/spring-security/reference/reactive/configuration/webflux.html
     *
     * @param http
     * @return {@link SecurityWebFilterChain}
     * @throws Exception
     */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http.csrf().disable().cors().and().authorizeExchange((exchange) -> {
            exchange.pathMatchers("/api/v0/token-service/**").permitAll();
        });

        http.csrf().disable().cors().and()
                .authorizeExchange(
                        ((exchange) -> exchange.pathMatchers("/api/v0/auth-service/**").permitAll()
                                .pathMatchers("/actuator/**").permitAll()
                                .anyExchange().hasAnyAuthority(Roles.ADMIN, Roles.USER, Roles.ANONYMOUS)
                               )
               )
                .oauth2ResourceServer(
                        (oauth2) -> oauth2.jwt().jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                );
        return http.build();
    }


    /**
     * This is changing the AuthenticationConverter from the "scope_" attribute
     * inside the jwt token to the custom added "Role" field inside the token
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


    /**
     * For WebFlux applications,
     * there is a different type of converter for JWTs necessary
     * that sets an Object of class {@link GrantedAuthoritiesExtractor}
     * Reference: https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html#webflux-oauth2resourceserver-jwt-authorization-extraction
     * @return
     */
    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = jwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new GrantedAuthoritiesExtractor());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    /**
     * the issuer uri had to be specified which is loaded from the application.yml
     * with the @Value annotation in the constructor and set as the documentation describe
     *  https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html#webflux-oauth2resourceserver-decoder-bean
     * @return
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(issuerUri).webClient(webClient()).build();
    }


    /**
     * THIS ONE IS FOR DOCKER ON MAC OS X AS THERE IS A PROBLEM
     * WITH INSERTING THE KEYSTORE ON MAC OS X INTO THIS WEBCLIENT
     * AND WE CIRCUMVENT THIS PROBLEM BY DIRECTLY SETTING THE KEY AND TRUSTSTORE
     * IN THIS FUNCTION
     *
     * Since the JWTDecoder needs to have a webclient that does generate a client certificate to
     * authenticate with the authorization service a webclient that inserts the ssl-certificates is needed.
     * The .yml configuration is not working as the certificates then will not be included in the netty client
     * the api-gateway is using
     *
     * The netty webclient that is used to access the public key from the authorization server
     * due to some reason does not grab the key- and trust store that we set up in the ConfigurationJava.class
     * and I found multiple issues with that and there was not a lot of documentation available at all
     * for this case and due to that the gateway was not able to authenticate with
     * other servers
     *
     * A github issue where this problem was talked about
     * helped me immensely to get this problem sorted out
     *
     * The code below is taken from the issues page
     * and as far as I know and found out about
     * currently the only way to implement the mTLS between the gateway and
     * the authorization server to authenticate the tokens that are sent
     * within the requests to the gateway service
     *
     * Link to Code Source: https://github.com/reactor/reactor-netty/issues/640
     * Comment by ealexhaywood on Apr 1, 2019 includes the WebClient implementation tht is
     * used here.
     * @return {@link WebClient}
     */
    @Profile("docker")
    public WebClient webClient() {

        HttpClient httpClient = HttpClient.create().secure(sslContextSpec ->
                {
                    try {
                        /* retrieving the locations as string from where we put the key and truststore in
                         {@link ConfigurationSSL.class*/

                        System.setProperty("javax.net.ssl.keyStore", "./microservices.p12");
                        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
                        System.setProperty("javax.net.ssl.trustStore", "./microservices.p12");
                        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

                        String keyStoreLocation = System.getProperty("javax.net.ssl.keyStore");
                        String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
                        String trustStoreLocation = System.getProperty("javax.net.ssl.trustStore");
                        String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");

                        // creating a keystore with the keyStoreLocation and password
                        KeyStore keyStore = KeyStore.getInstance("PKCS12");
                        keyStore.load(new FileInputStream(ResourceUtils.getFile(keyStoreLocation)), keyStorePassword.toCharArray());

                        // creating a KeyManagerFactory
                        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

                        // creating a truststore with location and password
                        KeyStore trustStore = KeyStore.getInstance("PKCS12");
                        trustStore.load(new FileInputStream((ResourceUtils.getFile(trustStoreLocation))), trustStorePassword.toCharArray());

                        // creating a TrustManagerFactory
                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                        trustManagerFactory.init(trustStore);

                        // including it in the current SSL context
                        sslContextSpec
                                .sslContext(SslContextBuilder.forClient()
                                        .keyManager(keyManagerFactory)
                                        .trustManager(trustManagerFactory).build());
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
        );
        // returning the WebClient that includes the SSL configuration and inserting it in JwtDecoder so mTLS between
        // downstream services and especially the authorization server works
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    /**
     * Since the JWTDecoder needs to have a webclient that does generate a client certificate to
     * authenticate with the authorization service a webclient that inserts the ssl-certificates is needed.
     * The .yml configuration is not working as the certificates then will not be included in the netty client
     * the api-gateway is using
     *
     * The netty webclient that is used to access the public key from the authorization server
     * due to some reason does not grab the key- and trust store that we set up in the ConfigurationJava.class
     * and I found multiple issues with that and there was not a lot of documentation available at all
     * for this case and due to that the gateway was not able to authenticate with
     * other servers
     *
     * A github issue where this problem was talked about
     * helped me immensely to get this problem sorted out
     *
     * The code below is taken from the issues page
     * and as far as I know and found out about
     * currently the only way to implement the mTLS between the gateway and
     * the authorization server to authenticate the tokens that are sent
     * within the requests to the gateway service
     *
     * Link to Code Source: https://github.com/reactor/reactor-netty/issues/640
     * Comment by ealexhaywood on Apr 1, 2019 includes the WebClient implementation tht is
     * used here.
     * @return {@link WebClient}
     */
    @Profile("dev")
    public WebClient webClientDev() {

        HttpClient httpClient = HttpClient.create().secure(sslContextSpec ->
                {
                    try {
                        /* retrieving the locations as string from where we put the key and truststore in
                         {@link ConfigurationSSL.class*/

                        System.setProperty("javax.net.ssl.keyStore", "./microservices.p12");
                        System.out.println(System.getProperty("javax.net.ssl.keyStore", "./microservices.p12"));
                        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
                        System.setProperty("javax.net.ssl.trustStore", "./microservices.p12");
                        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

                        String keyStoreLocation = System.getProperty("javax.net.ssl.keyStore");
                        String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
                        String trustStoreLocation = System.getProperty("javax.net.ssl.trustStore");
                        String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");

                        // creating a keystore with the keyStoreLocation and password
                        KeyStore keyStore = KeyStore.getInstance("PKCS12");
                        keyStore.load(new FileInputStream(ResourceUtils.getFile(keyStoreLocation)), keyStorePassword.toCharArray());

                        // creating a KeyManagerFactory
                        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

                        // creating a truststore with location and password
                        KeyStore trustStore = KeyStore.getInstance("PKCS12");
                        trustStore.load(new FileInputStream((ResourceUtils.getFile(trustStoreLocation))), trustStorePassword.toCharArray());

                        // creating a TrustManagerFactory
                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                        trustManagerFactory.init(trustStore);

                        // including it in the current SSL context
                        sslContextSpec
                                .sslContext(SslContextBuilder.forClient()
                                        .keyManager(keyManagerFactory)
                                        .trustManager(trustManagerFactory).build());
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
        );
        // returning the WebClient that includes the SSL configuration and inserting it in JwtDecoder so mTLS between
        // downstream services and especially the authorization server works
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    /**
     * currently the only way to make a WebFlux Application to be stateless in
     * terms of creating a session id
     *
     * https://stackoverflow.com/a/67005365
     * @return {@link WebSessionManager}
     */
    @Bean
    public WebSessionManager webSessionManager() {
        return exchange -> Mono.empty();
    }
}

/**
 * As to extract authorities in WebFlux a specific class is necessary to implement
 * https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html#webflux-oauth2resourceserver-jwt-authorization-extraction
 */
class GrantedAuthoritiesExtractor
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    Logger logger = LoggerFactory.getLogger(GrantedAuthoritiesExtractor.class);

    public Collection<GrantedAuthority> convert(Jwt jwt) {

       List<String> authorities = new ArrayList<>();

       authorities.add( "ROLE_" + jwt.getClaims().get("role").toString()
               .replace("[\"", "")
               .replace("\"]", ""));

       logger.info(authorities.toString());

        return authorities.stream()
                .map(Object::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}