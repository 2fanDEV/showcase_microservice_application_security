package de.security.microservice.commentservice.feign.blogServiceFeign;

import de.security.microservice.commentservice.feign.blogServiceFeign.configuration.BlogServiceFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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
@FeignClient(name = "BLOG-SERVICE", configuration = BlogServiceFeignConfiguration.class)
public interface BlogServiceFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/services/blog-service/doesBlogExist")
    Boolean doesBlogExist(@RequestParam Long blogId);

}
