package de.security.microservice.blogservice.Controller.BlogArticleController;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.security.microservice.blogservice.Repository.Model.dto.BlogArticleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

public interface BlogArticleControllerInterface {

    ResponseEntity<Boolean> createBlogArticle(HttpServletRequest httpServletRequest, String dto) throws JsonProcessingException;

    ResponseEntity<Boolean> deleteBlogArticle(HttpServletRequest httpServletRequest, @RequestBody String username,@PathVariable("id") Long id) throws ParseException;

    ResponseEntity<List<BlogArticleDTO>> getAllArticles();

    @PostMapping("/getAllBlogArticlesById")
    ResponseEntity<List<BlogArticleDTO>> getAllArticlesById(List<Long> ids);

    @GetMapping("/getAllBlogArticlesLikedByUserName")
    ResponseEntity<List<BlogArticleDTO>> getAllArticlesLikedByUserName(@RequestParam("username") String username);

    @GetMapping("/getAllBlogArticlesByUsername")
    ResponseEntity<List<BlogArticleDTO>> getAllArticlesByUsername(@RequestParam("username") String username);

    @GetMapping("/getAllBlogsUserCommentedOn")
    ResponseEntity<List<BlogArticleDTO>> getAllBlogsUserCommentedOn(@RequestParam("username") String username);

}
