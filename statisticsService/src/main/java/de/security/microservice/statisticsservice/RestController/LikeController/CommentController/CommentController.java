package de.security.microservice.statisticsservice.RestController.LikeController.CommentController;

import de.security.microservice.statisticsservice.services.CommentService.CommentService;
import de.security.microservice.statisticsservice.services.ServletService.ServletService;
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
@RequestMapping("api/services/statistics-service/comment")
public class CommentController implements CommentControllerInterface {

    CommentService commentService;

    ServletService servletService;

    @Autowired
    CommentController(CommentService commentService, ServletService servletService)
    {
        this.commentService = commentService;
        this.servletService = servletService;
    }

    /**
     * like a specific comment
     * @param httpServletRequest
     * @param authorName
     * @param commentId
     * @param blogId
     * @return
     * @throws Exception
     */
    @Override
    @PostMapping("/likeComment")
    public ResponseEntity<String> likeComment(HttpServletRequest httpServletRequest, @RequestParam("authorName") String authorName, @RequestParam("commentId") Long commentId, @RequestParam("blogId") Long blogId) throws Exception {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer", "");
        String username = servletService.getUserNameFromToken(token);

        commentService.likeComment(username, authorName, commentId, blogId);

        return ResponseEntity.ok("Like/Unlike was executed");
    }

    /**
     * get total amount of likes for comments by a username
     * @param username
     * @return
     */
    @Override
    @GetMapping("/likesForCommentsByUserName")
    public ResponseEntity<List<Long>> likesForBlogByUserName(@RequestParam("username") String username)
    {
        return ResponseEntity.ok(commentService.likesForCommentByUserName(username));
    }

    /**
     * get total amount of likes for comments
     * @param commentIds
     * @return
     */
    @Override
    @PostMapping("/getAllCommentLikeCounter")
    public ResponseEntity<Map<Long, Long>> getAllCommentLikeCounter(@RequestBody List<Long> commentIds)
    {
        return ResponseEntity.ok(commentService.likesForCommentIds(commentIds));
    }
}
