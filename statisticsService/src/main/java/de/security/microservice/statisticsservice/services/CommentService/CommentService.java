package de.security.microservice.statisticsservice.services.CommentService;

import de.security.microservice.statisticsservice.feign.BlogServiceFeign.BlogServiceFeignClient;
import de.security.microservice.statisticsservice.feign.userServiceFeign.UserServiceFeignClient;
import de.security.microservice.statisticsservice.Model.commentLikes.CommentLike;
import de.security.microservice.statisticsservice.Model.compositeKeys.CommentKey;
import de.security.microservice.statisticsservice.Repository.BlogLikeRepository;
import de.security.microservice.statisticsservice.Repository.CommentLikeRepository;
import de.security.microservice.statisticsservice.feign.CommentServiceFeign.CommentServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The @Service annotation makes this service
 * enabled for the @Autowired annotation, so we can inject
 * this service in the constructor of a Controller for example
 *
 * Ref:
 * https://docs.spring.io/spring-ws/docs/current/reference/html/#_providing_the_service_and_stub_implementation
 */
@Service
public class CommentService implements CommentServiceInterface {

    CommentLikeRepository commentLikeRepository;
    BlogLikeRepository blogLikeRepository;
    BlogServiceFeignClient blogServiceFeignClient;
    CommentServiceFeignClient commentServiceFeignClient;
    UserServiceFeignClient userServiceFeignClient;

    /**
     * constructor with blog and comment repository
     * and feign clients
     * @param commentLikeRepository
     * @param blogServiceFeignClient
     * @param commentServiceFeignClient
     * @param userServiceFeignClient
     * @param blogLikeRepository
     */
    @Autowired
    CommentService(CommentLikeRepository commentLikeRepository,
                   BlogServiceFeignClient blogServiceFeignClient,
                   CommentServiceFeignClient commentServiceFeignClient,
                   UserServiceFeignClient userServiceFeignClient,
                   BlogLikeRepository blogLikeRepository)
    {
        this.commentLikeRepository = commentLikeRepository;
        this.blogServiceFeignClient = blogServiceFeignClient;
        this.commentServiceFeignClient = commentServiceFeignClient;
        this.userServiceFeignClient = userServiceFeignClient;
        this.blogLikeRepository = blogLikeRepository;
    }

    /**
     * like a specific comment
     * @param userName
     * @param authorName
     * @param commentId
     * @param blogId
     * @throws Exception
     */
    @Override
    public void likeComment(String userName, String authorName, Long commentId, Long blogId) throws Exception {
        CommentKey commentKey = new CommentKey();
        commentKey.setCommentId(commentId);
        commentKey.setBlogId(blogId);
        commentKey.setUserName(userName);


        if (!userServiceFeignClient.doesUserExist(userName).getBody()) {
            throw new Exception("User doesn't exist!");
        }

        if(blogLikeRepository.findFirstByBlogId(blogId) == null) {
            if (!blogServiceFeignClient.doesBlogExist(blogId, userName).getBody()) {
                throw new Exception("Blog doesn't exist");
            }
        }
        if(commentLikeRepository.findFirstByCommentId(commentId) == null) {
            if (!commentServiceFeignClient.doesCommentExist(commentId, userName).getBody())
            {
                throw new Exception("Comment doesn't exist");
            }
        }

        if(commentLikeRepository.findById(commentKey).isPresent())
        {
            commentLikeRepository.deleteById(commentKey);
        } else {
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentKey(commentKey);
            commentLike.setLikeTimeStamp(Date.from(Instant.now()));
            commentLike.setAuthorName(authorName);
            commentLikeRepository.save(commentLike);
        }
    }

    /**
     * get all likes for commentIds
     * @param commentIds
     * @return
     */
    @Override
    public Map<Long, Long> likesForCommentIds(List<Long> commentIds)
    {
        Map<Long, Long> likeForCommentIds = new HashMap<>();
        for(Long commentId : commentIds)
        {
            likeForCommentIds.put(commentId, (long) commentLikeRepository.findCommentLikesByCommentId(commentId).size());
        }

        return likeForCommentIds;
    }

    /**
     * get all liked comments by user name
     * @param username
     * @return
     */
    @Override
    public List<Long> likesForCommentByUserName (String username) {
        return commentLikeRepository.findCommentLikesByUserName(username).stream().map(commentLike ->  commentLike.getCommentKey().getCommentId()).collect(Collectors.toList());
    }
}
