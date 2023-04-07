package de.security.microservice.statisticsservice.feign.userServiceFeign;

import de.security.microservice.statisticsservice.feign.config.GeneralFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This is a class solely for a the FeignClient to
 * send a http request to another service when using one of the
 * defined functions
 *
 * @FeignClient("COMMENT-SERVICE") means that the server retrieves the
 * URI from the Eureka Server if an entry with the given name is existent
 *
 * https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html
 */
@FeignClient(name = "USER-SERVICE",
            configuration = GeneralFeignConfiguration.class,
            fallback = UserServiceFeignClientFallBack.class)
public interface UserServiceFeignClient {

    @PostMapping(value = "/api/services/user-service/doesUserExist")
    ResponseEntity<Boolean> doesUserExist(@RequestParam String username);

}

