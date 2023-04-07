package de.security.microservice.authorizationserver.feign;

import de.security.microservice.authorizationserver.model.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
@FeignClient("USER-SERVICE")
public interface UserServiceFeign {
    @RequestMapping(method = RequestMethod.POST, value = "/api/services/user-service/registerUser", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO);

}
