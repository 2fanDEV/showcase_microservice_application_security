package de.security.microservice.statisticsservice.RestController.LikeController.CommentController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface CommentControllerInterface {
    @PostMapping("/likeComment")
    ResponseEntity<String> likeComment(HttpServletRequest httpServletRequest, @RequestParam("authorName") String authorName, @RequestParam("commentId") Long commentId, @RequestParam("blogId") Long blogId) throws Exception;

    @GetMapping("/likesForBlogsByUserName")
    ResponseEntity<List<Long>> likesForBlogByUserName(@RequestParam("username") String username);

    @PostMapping("/getAllCommentLikeCounter")
    ResponseEntity<Map<Long, Long>> getAllCommentLikeCounter(@RequestBody List<Long> commentIds);
}
