package de.security.microservice.statisticsservice.RestController.LikeController.BlogController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface BlogControllerInterface {


    @GetMapping("/likeBlog")
    ResponseEntity<String> likeBlog(HttpServletRequest servletRequest, @RequestParam("authorName") String authorName, @RequestParam("blogId") Long blogId) throws Exception;

    @GetMapping("/likesForBlogsByUserName")
    ResponseEntity<List<Long>> likesForBlogByUserName(@RequestParam("username") String username);

    @PostMapping("/getAllBlogLikeCounter")
    ResponseEntity<Map<Long, Long>> getAllBlogLikeCounter(@RequestBody List<Long> blogIds);
}
