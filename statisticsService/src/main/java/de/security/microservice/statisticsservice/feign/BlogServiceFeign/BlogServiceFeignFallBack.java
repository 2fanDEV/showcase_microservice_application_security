package de.security.microservice.statisticsservice.feign.BlogServiceFeign;


import org.springframework.http.ResponseEntity;


/**
 * these is a fallback service which implements
 * the feign interface functions in case the approached server
 * doesnt respond, such that these are being executed
 *
 * this does not work yet
 *
 * https://cloud.spring.io/spring-cloud-openfeign/reference/html/#spring-cloud-feign-hystrix-fallback
 *
 * This is probably due to the fact that hystrix is not enabled and
 * since hystrix is deprecated I did not want to use it but did
 * not find a replacement for it
 */
public class BlogServiceFeignFallBack implements BlogServiceFeignClient {

    @Override
    public ResponseEntity<Long> getNumberOfBlogsByUserName(String userName) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> doesBlogExist(Long blogId, String userName) {
        return ResponseEntity.ok(false);
    }
}
