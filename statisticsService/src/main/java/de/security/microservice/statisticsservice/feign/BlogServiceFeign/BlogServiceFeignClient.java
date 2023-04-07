package de.security.microservice.statisticsservice.feign.BlogServiceFeign;

import de.security.microservice.statisticsservice.feign.config.GeneralFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This is a class solely for a the FeignClient to
 * send a http request to another service when using one of the
 * defined functions
 *
 * @FeignClient("BLOG-SERVICE") means that the server retrieves the
 * URI from the Eureka Server if an entry with the given name is existent
 *
 * https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html
 */
@FeignClient(name = "BLOG-SERVICE",
        configuration = GeneralFeignConfiguration.class,
        fallback = BlogServiceFeignFallBack.class)
public interface BlogServiceFeignClient {

    @GetMapping("/api/services/blog-service/getBlogsByUserName")
    ResponseEntity<Long> getNumberOfBlogsByUserName(@RequestParam("username") String userName);

    @GetMapping("/api/services/blog-service/doesBlogExist")
    ResponseEntity<Boolean> doesBlogExist(@RequestParam("blogId") Long blogId, @RequestParam("username") String username);
}


