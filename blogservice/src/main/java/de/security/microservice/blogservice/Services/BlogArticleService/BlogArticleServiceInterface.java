package de.security.microservice.blogservice.Services.BlogArticleService;

import de.security.microservice.blogservice.Repository.Model.dto.BlogArticleDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * the interface for the blog article service
 */
public interface BlogArticleServiceInterface {

    ResponseEntity<Boolean> createBlog(BlogArticleDTO dto);

    ResponseEntity<Boolean> deleteBlog(String token, String username, long id);

    ResponseEntity<List<BlogArticleDTO>> getAllBlogArticles();

    ResponseEntity<List<BlogArticleDTO>> getAllBlogArticlesByUserName(String username);

    ResponseEntity<Boolean> editBlog(BlogArticleDTO blogArticleDTO);

    ResponseEntity<List<BlogArticleDTO>> getAllBlogArticlesLikedByUsername(String username);

    ResponseEntity<List<BlogArticleDTO>> getAllBlogsUserCommentedOn(String username);

    ResponseEntity<Boolean> doesBlogExistByBlogIdAndUserName(Long blogId, String username);

    ResponseEntity<Long> getNumberOfBlogsByUserName(String username);

    ResponseEntity<List<BlogArticleDTO>> getAllBlogsById(List<Long> ids);
}
