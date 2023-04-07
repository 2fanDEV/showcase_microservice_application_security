package de.security.microservice.blogservice.feign.UserServiceFeign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This is a class solely for a the FeignClient to
 * send a http request to another service when using one of the
 * defined functions
 *
 * @FeignClient("USER-SERVICE") means that the server retrieves the
 * URI from the Eureka Server if an entry with the given name is existent
 *
 * https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html
 */
@FeignClient(name = "USER-SERVICE", configuration = UserServiceFeignConfiguration.class)
public interface UserServiceFeignClient {

    @PostMapping(value = "/api/services/user-service/doesUserExist")
    ResponseEntity<Boolean> doesUserExist(@RequestParam String username);


}
