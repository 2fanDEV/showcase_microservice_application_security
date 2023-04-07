package de.security.microservice.blogservice.feign.CommentServiceFeign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
@FeignClient(value = "COMMENT-SERVICE", configuration = CommentServiceFeignConfiguration.class)
public interface CommentServiceFeign {

    @GetMapping("/api/services/like-service/getBlogIdLikedByUserName")
    ResponseEntity<List<Long>> getLikedBlogIdsByUserName(@RequestParam(name = "username") String username);

    @GetMapping("/api/services/comment-service/getCommentedBlogIdsByUserComment")
    ResponseEntity<List<Long>> getCommentedBlogIdsByUserComment(@RequestParam(name = "username") String username);

    @PostMapping("/api/services/comment-service/deleteAllCommentBlogsById")
    ResponseEntity<Boolean> deleteAllCommentsByBlogId(@RequestParam(name = "blogId") Long blogId, String username);
}
