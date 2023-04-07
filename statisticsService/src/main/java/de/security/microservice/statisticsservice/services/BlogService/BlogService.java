package de.security.microservice.statisticsservice.services.BlogService;

import de.security.microservice.statisticsservice.Model.compositeKeys.BlogKey;
import de.security.microservice.statisticsservice.Repository.BlogLikeRepository;
import de.security.microservice.statisticsservice.feign.BlogServiceFeign.BlogServiceFeignClient;
import de.security.microservice.statisticsservice.feign.userServiceFeign.UserServiceFeignClient;
import de.security.microservice.statisticsservice.Model.blogLike.BlogLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
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
public class BlogService implements BlogServiceInterface{

    BlogLikeRepository blogLikeRepository;

    UserServiceFeignClient userServiceFeignClient;

    BlogServiceFeignClient blogServiceFeignClient;

    /**
     * Constructor with feign clients and repository
     * @param blogLikeRepository
     * @param userServiceFeignClient
     * @param blogServiceFeignClient
     */
    @Autowired
    BlogService(BlogLikeRepository blogLikeRepository,
                UserServiceFeignClient userServiceFeignClient,
                BlogServiceFeignClient blogServiceFeignClient)
    {
        this.blogLikeRepository = blogLikeRepository;
        this.userServiceFeignClient = userServiceFeignClient;
        this.blogServiceFeignClient = blogServiceFeignClient;
    }

    /**
     * like a blog
     * @param username
     * @param authorName
     * @param blogId
     * @return
     * @throws Exception
     */
    @Override
    public ResponseEntity<String> likeBlog(String username, String authorName, Long blogId) throws Exception {
        BlogKey blogKey = new BlogKey();
        blogKey.setBlogId(blogId);
        blogKey.setUserName(username);

        if(!userServiceFeignClient.doesUserExist(username).getBody())
            throw new UserPrincipalNotFoundException("user does not exist");
        if(!blogServiceFeignClient.doesBlogExist(blogId, authorName).getBody())
            throw new Exception("Blog does not exist");

        if(blogLikeRepository.findById(blogKey).isPresent())
        {
            blogLikeRepository.deleteById(blogKey);
            return ResponseEntity.ok("Removed Like From Selected Blog");
        } else
        {
            BlogLike blogLike = new BlogLike();
            blogLike.setBlogKey(blogKey);
            blogLike.setAuthorName(authorName);
            blogLike.setLikeTimeStamp(Date.from(Instant.now()));
            blogLikeRepository.save(blogLike);
        }

        return ResponseEntity.ok("Selected Blog liked");
    }

    /**
     * get all liked blogs by username
     * @param username
     * @return
     */
    @Override
    public List<Long> likesForBlogByUserName(String username) {

        return blogLikeRepository.findBlogLikesByUserName(username).stream().map(blogLike ->  blogLike.getBlogKey().getBlogId()).collect(Collectors.toList());
    }

    /**
     * get all likes for specific blog ids
     * @param blogIds
     * @return
     */
    @Override
    public Map<Long, Long> likesForBlogIds(List<Long> blogIds)
    {
        Map<Long, Long> likeForBlogIds = new HashMap<>();
        for(Long blogId : blogIds)
        {
            likeForBlogIds.put(blogId, (long) blogLikeRepository.findBlogLikesByBlogId(blogId).size());
        }

        return likeForBlogIds;
    }
}
