package de.security.microservice.statisticsservice.services.UserService;

import de.security.microservice.statisticsservice.feign.BlogServiceFeign.BlogServiceFeignClient;
import de.security.microservice.statisticsservice.Model.UserStats.UserStats;
import de.security.microservice.statisticsservice.Repository.BlogLikeRepository;
import de.security.microservice.statisticsservice.Repository.CommentLikeRepository;
import de.security.microservice.statisticsservice.feign.CommentServiceFeign.CommentServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The @Service annotation makes this service
 * enabled for the @Autowired annotation, so we can inject
 * this service in the constructor of a Controller for example
 *
 * Ref:
 * https://docs.spring.io/spring-ws/docs/current/reference/html/#_providing_the_service_and_stub_implementation
 */
@Service
public class UserService implements UserServiceInterface {

    BlogLikeRepository blogLikeRepository;

    CommentLikeRepository commentLikeRepository;

    BlogServiceFeignClient blogServiceFeignClient;

    CommentServiceFeignClient commentServiceFeignClient;

    /**
     * Constructor with repositories and Feign Clients
     * @param blogLikeRepository
     * @param commentLikeRepository
     * @param blogServiceFeignClient
     * @param commentServiceFeignClient
     */
    @Autowired
    UserService(BlogLikeRepository blogLikeRepository,
                CommentLikeRepository commentLikeRepository,
                BlogServiceFeignClient blogServiceFeignClient,
                CommentServiceFeignClient commentServiceFeignClient)
    {
        this.blogLikeRepository = blogLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.blogServiceFeignClient = blogServiceFeignClient;
        this.commentServiceFeignClient = commentServiceFeignClient;
    }

    /**
     * compile the statistics of a user and return it in an object
     * @param userName
     * @return
     */
    public UserStats userStats(String userName)
    {
        UserStats userStats = new UserStats();
        Long givenLikesForBlogs = (long) blogLikeRepository.findBlogLikesByUserName(userName).size();
        Long receivedLikesForBlogs = (long) blogLikeRepository.findBlogLikesByAuthorName(userName).size();
        Long givenLikesForComments = (long) commentLikeRepository.findCommentLikesByUserName(userName).size();
        Long receivedLikesForComments = (long) commentLikeRepository.findCommentLikesByAuthorName(userName).size();
        userStats.setGivenLikesForBlogs(givenLikesForBlogs);
        userStats.setReceivedLikesForBlogs(receivedLikesForBlogs);
        userStats.setGivenLikesForComments(givenLikesForComments);
        userStats.setReceivedLikesForComments(receivedLikesForComments);
        userStats.setBlogsPosted(blogServiceFeignClient.getNumberOfBlogsByUserName(userName).getBody());
        userStats.setCommentsPosted(commentServiceFeignClient.getNumberOfCommentsByUserName(userName).getBody());
        return userStats;
    }


}
