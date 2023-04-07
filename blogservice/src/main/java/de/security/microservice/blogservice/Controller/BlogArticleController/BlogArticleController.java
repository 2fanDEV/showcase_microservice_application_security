package de.security.microservice.blogservice.Controller.BlogArticleController;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.security.microservice.blogservice.Repository.Model.dto.BlogArticleDTO;
import de.security.microservice.blogservice.feign.UserServiceFeign.UserServiceFeignClient;
import de.security.microservice.blogservice.Services.BlogArticleService.BlogArticleService;
import de.security.microservice.blogservice.Services.ServletService.ServletService;
import de.security.microservice.blogservice.Services.ServletService.ServletServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.text.ParseException;
import java.util.List;

/**
 * REST Controller for the comments for blogs
 *
 * I already knew how to do this, however
 * here is the reference in the documentation
 *
 * Ref:
 * https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller
 *
 * This is a controller for the blog service endpoints
 */
@RestController
@RequestMapping(value = "/api/services/blog-service")
public class BlogArticleController implements BlogArticleControllerInterface {

    Logger logger = LoggerFactory.getLogger(BlogArticleController.class);
    public final BlogArticleService blogArticleService;
    public final UserServiceFeignClient userServiceFeignClient;
    public final ServletServiceInterface servletService;

    @Autowired
    BlogArticleController(BlogArticleService blogArticleService, ServletService servletService, UserServiceFeignClient userServiceFeignClient)
    {
        this.blogArticleService = blogArticleService;
        this.servletService = servletService;
        this.userServiceFeignClient = userServiceFeignClient;
    }

    /**
     * creating a blog
     *
     * @param httpServletRequest
     * @param dtoString
     * @return {@link ResponseEntity<Boolean>}
     * @throws JsonProcessingException
     */
    @Override
    @PostMapping(value = "/createBlog")
    public ResponseEntity<Boolean> createBlogArticle(HttpServletRequest httpServletRequest,
                                                    @RequestParam("blogArticle") String dtoString)
    throws JsonProcessingException {
        BlogArticleDTO dto = getObjectMapper().readValue(dtoString, BlogArticleDTO.class);
        try {
            if (servletService.getTokenFromRequestAndCheckUser(httpServletRequest)) {
                logger.info(httpServletRequest.getQueryString());
                logger.info(httpServletRequest.getParameterMap().values().toString());
                logger.info("BLOGARTICLEDTO: " + dto.toString());
                return blogArticleService.createBlog(dto);
            }
        } catch (Exception e) {
            logger.error("Internal Server Error");
        }
        return ResponseEntity.internalServerError().build();
    }

    /**
     * Deleting a specific blog by id
     * @param httpServletRequest
     * @param username
     * @param id
     * @return {@link ResponseEntity<Boolean>}
     * @throws ParseException
     */
    @Override
    @PostMapping("/deleteBlogArticle/{id}")
    public ResponseEntity<Boolean> deleteBlogArticle(HttpServletRequest httpServletRequest, @RequestBody String username, @PathVariable("id") Long id)
    throws ParseException {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer", " ");
        String tokenUserName = servletService.getUserNameFromToken(token);
        if (!username.equals(tokenUserName)
                || Boolean.FALSE.equals(this.userServiceFeignClient.doesUserExist(username).getBody()))
        {
            return ResponseEntity.internalServerError().build();
        }
        return blogArticleService.deleteBlog(token,username, id);
    }

    /**
     * getting all blogs for the feed in frontend
     * @return {@link ResponseEntity<List<BlogArticleDTO>>}
     */
    @Override
    @GetMapping("/getAllBlogArticles")
    public ResponseEntity<List<BlogArticleDTO>> getAllArticles() {

        return blogArticleService.getAllBlogArticles();
    }

    @Override
    @PostMapping("/getAllBlogArticlesById")
    public ResponseEntity<List<BlogArticleDTO>> getAllArticlesById(@RequestBody List<Long> ids) {
        return blogArticleService.getAllBlogsById(ids);
    }

    /**
     * Retrieving all liked blog articles by a specific user
     * @param username
     * @return {@link ResponseEntity<List<BlogArticleDTO>>}
     */
    @Override
    @GetMapping("/getAllBlogArticlesLikedByUserName")
    public ResponseEntity<List<BlogArticleDTO>> getAllArticlesLikedByUserName(@RequestParam(name = "username") String username)
    {
        logger.info("GET ALL BLOG ARTICLES LIKED BY USERNAME");
        return blogArticleService.getAllBlogArticlesLikedByUsername(username);
    }

    /**
     * getting all blogs a specific user has made
     * @param username
     * @return {@link ResponseEntity<List<BlogArticleDTO>>}
     */
    @Override
    @GetMapping("/getAllBlogArticlesByUserName")
    public ResponseEntity<List<BlogArticleDTO>> getAllArticlesByUsername(@RequestParam("username") String username)
    {
        return blogArticleService.getAllBlogArticlesByUserName(username);
    }

    /**
     * get all articles a specific user has commented on
     * @param username
     * @return {@link ResponseEntity<List<BlogArticleDTO>>}
     */
    @Override
    @GetMapping("/getAllBlogsUserCommentedOn")
    public ResponseEntity<List<BlogArticleDTO>> getAllBlogsUserCommentedOn(@RequestParam("username") String username)
    {
        return blogArticleService.getAllBlogsUserCommentedOn(username);
    }

    /**
     * the object mapper is there to convert a JSON String to a entity
     * @return {@link ObjectMapper}
     */
    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    /**
     * checking if a blog exists for id and username
     * @param blogId
     * @param username
     * @return
     */
    @GetMapping("/doesBlogExist")
    public ResponseEntity<Boolean> doesBlogExistByBlogIdAndUserName(@RequestParam("blogId") Long blogId, @RequestParam("username")  String username)
    {
        return blogArticleService.doesBlogExistByBlogIdAndUserName(blogId, username);
    }

    /**
     * retrieving the number of blgos an individual has posted
     * @param username
     * @return
     */
    @GetMapping("/getBlogsByUserName")
    public ResponseEntity<Long> getBlogAmountByUserName(@RequestParam("username") String username)
    {
        return blogArticleService.getNumberOfBlogsByUserName(username);
    }


}
