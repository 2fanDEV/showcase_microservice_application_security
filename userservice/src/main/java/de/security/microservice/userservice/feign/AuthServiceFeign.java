package de.security.microservice.userservice.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This is a class solely for a the FeignClient to
 * send a http request to another service when using one of the
 * defined functions
 *
 * @FeignClient("AUTHORIZATION-SERVICE") means that the server retrieves the
 * URI from the Eureka Server if an entry with the given name is existent
 *
 * https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html
 */
@FeignClient(name = "AUTHORIZATION-SERVICE", configuration = AuthServerFeignConfiguration.class)
public interface AuthServiceFeign {

    @GetMapping(value = "/api/services/auth-service/getUser")
    Boolean doesUserExist(@RequestParam String username);

}
