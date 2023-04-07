package de.security.microservice.statisticsservice.RestController.LikeController.BlogController;

import de.security.microservice.statisticsservice.feign.userServiceFeign.UserServiceFeignClient;
import de.security.microservice.statisticsservice.services.BlogService.BlogService;
import de.security.microservice.statisticsservice.services.ServletService.ServletService;
import de.security.microservice.statisticsservice.services.UserService.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for the comments for blogs
 *
 * I already knew how to do this, however
 * here is the reference in the documentation
 *
 * Ref:
 * https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller
 *
 * This is a controller for the endpoints of the comment service
 *
 */
@RestController
@RequestMapping("/api/services/statistics-service/blog")
public class BlogController implements BlogControllerInterface {
    Logger logger = LoggerFactory.getLogger(BlogController.class);
    private final UserServiceFeignClient userServiceFeignClient;
    private final BlogService blogService;
    private final UserService userService;
    private final ServletService servletService;

    @Autowired
    BlogController(UserServiceFeignClient userServiceFeignClient,
                   BlogService blogService,
                   UserService userService,
                   ServletService servletService)
    {
        this.userServiceFeignClient = userServiceFeignClient;
        this.blogService = blogService;
        this.userService = userService;
        this.servletService = servletService;
    }

    /**
     * whenever a blog gets liked this function gets executed
     * @param servletRequest
     * @param authorName
     * @param blogId
     * @return
     * @throws Exception
     */
    @Override
    @PostMapping("/likeBlog")
    public ResponseEntity<String> likeBlog(HttpServletRequest servletRequest, @RequestParam("authorName") String authorName, @RequestParam("blogId") Long blogId) throws Exception {
        String token = servletRequest.getHeader("Authorization").replace("Bearer", "");
        String username = servletService.getUserNameFromToken(token);

        blogService.likeBlog(username, authorName, blogId);

        return ResponseEntity.ok("Blog successfully liked");
    }

    /**
     * get amount of liked blogs for a user
     * @param username
     * @return
     */
    @Override
    @GetMapping("/likesForBlogsByUserName")
    public ResponseEntity<List<Long>> likesForBlogByUserName(@RequestParam("username") String username)
    {
        return ResponseEntity.ok(blogService.likesForBlogByUserName(username));
    }

    /**
     * get amount of total received likes
     * @param blogIds
     * @return
     */
    @Override
    @PostMapping("/getAllBlogLikeCounter")
    public ResponseEntity<Map<Long, Long>> getAllBlogLikeCounter(@RequestBody List<Long> blogIds)
    {
        return ResponseEntity.ok(blogService.likesForBlogIds(blogIds));
    }


}
