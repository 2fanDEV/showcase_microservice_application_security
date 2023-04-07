package de.security.microservice.blogservice.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Another configuration class where we set the
 * trust and keystore for the spring server
 * We are doing it this way since this way there is no problem with configuration
 * while trying to set it up on your own machine
 * Ref: https://stackoverflow.com/questions/2138574/java-path-to-truststore-set-property-doesnt-work
 *  https://docs.oracle.com/javadb/10.8.3.0/adminguide/cadminsslclient.html
 *  https://www.baeldung.com/spring-boot-https-self-signed-certificate
 */
@Configuration
public class ConfigurationSSL {

    @Bean
    @Profile("dev")
    void configureSSL()
    {
        System.setProperty("javax.net.ssl.keyStore", "src/main/resources/microservices.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStore", "src/main/resources/microservices.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
    }


    @Bean
    @Profile("docker")
    void configureSSLDocker()
    {
        System.out.println(System.getProperty("user.dir"));
        System.out.println(ConfigurationSSL.class.getResource("").getPath());
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is: " + s);

        System.setProperty("javax.net.ssl.keyStore", "./microservices.p12");
        System.out.println(System.getProperty("javax.net.ssl.keyStore", "./microservices.p12"));
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStore", "./microservices.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
    }

}
