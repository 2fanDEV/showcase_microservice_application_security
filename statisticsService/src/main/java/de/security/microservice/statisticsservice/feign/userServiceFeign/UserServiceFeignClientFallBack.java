package de.security.microservice.statisticsservice.feign.userServiceFeign;


import de.security.microservice.statisticsservice.Repository.BlogLikeRepository;
import de.security.microservice.statisticsservice.Repository.CommentLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserServiceFeignClientFallBack implements UserServiceFeignClient {

    BlogLikeRepository blogLikeRepository;
    CommentLikeRepository commentLikeRepository;

    @Autowired
    UserServiceFeignClientFallBack(BlogLikeRepository blogLikeRepository, CommentLikeRepository commentLikeRepository)
    {
        this.blogLikeRepository = blogLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    @Override
    public ResponseEntity<Boolean> doesUserExist(String username) {
        boolean dataBaseEntry = blogLikeRepository.findBlogLikesByUserName(username).isEmpty();
        if(!dataBaseEntry)
            dataBaseEntry = commentLikeRepository.findCommentLikesByUserName(username).isEmpty();
        return ResponseEntity.ok(dataBaseEntry);
    }
}
