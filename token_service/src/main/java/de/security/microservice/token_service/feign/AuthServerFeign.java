package de.security.microservice.token_service.feign;


import de.security.microservice.token_service.Configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * This is a class solely for a the FeignClient to
 * send a http request to another service when using one of the
 * defined functions
 *
 * @FeignClient("USER-SERVICE") means that the auth server retrieves the
 * URI from the Eureka Server if an entry with the given name is existent
 *
 * https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html
 */
@FeignClient(value = "AUTHORIZATION-SERVICE", configuration = FeignConfiguration.class)
public interface AuthServerFeign {

	@RequestMapping(method = RequestMethod.POST, value = "/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	String getAnonymousToken(@RequestBody String bodyParams);

	@RequestMapping(method = RequestMethod.POST, value = "/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	String getBearerToken(@RequestBody String bodyParams);

	@RequestMapping(method = RequestMethod.POST, value="/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	String getRefreshedBearerToken(@RequestBody String bodyParams);
}

