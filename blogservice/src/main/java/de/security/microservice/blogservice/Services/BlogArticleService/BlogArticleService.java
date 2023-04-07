package de.security.microservice.blogservice.Services.BlogArticleService;

import de.security.microservice.blogservice.Repository.Model.dto.BlogArticleDTO;
import de.security.microservice.blogservice.feign.CommentServiceFeign.CommentServiceFeign;
import de.security.microservice.blogservice.feign.UserServiceFeign.UserServiceFeignClient;
import de.security.microservice.blogservice.Config.FeignHelperService;
import de.security.microservice.blogservice.Repository.BlogArticleRepository;
import de.security.microservice.blogservice.Repository.Model.BlogArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import static java.util.stream.Collectors.toList;

/**
 * The @Service annotation makes this service
 * enabled for the @Autowired annotation, so we can inject
 * this service in the constructor of a Controller for example
 *
 * Ref:
 * https://docs.spring.io/spring-ws/docs/current/reference/html/#_providing_the_service_and_stub_implementation
 */
@Service
public class BlogArticleService implements BlogArticleServiceInterface {
    Logger logger = LoggerFactory.getLogger(BlogArticleService.class);
    BlogArticleRepository blogArticleRepository;
    CommentServiceFeign commentServiceFeign;
    UserServiceFeignClient userServiceFeign;
    FeignHelperService feignHelperService;

    @Autowired
    BlogArticleService(BlogArticleRepository blogArticleRepository,
                       CommentServiceFeign commentServiceFeign,
                       UserServiceFeignClient userServiceFeignClient,
                       FeignHelperService feignHelperService)
    {
        this.blogArticleRepository = blogArticleRepository;
        this.commentServiceFeign = commentServiceFeign;
        this.userServiceFeign = userServiceFeignClient;
        this.feignHelperService = feignHelperService;
    }


    /**
     * function to create an object of blog and save it into database
     * @param blogArticleDTO
     * @return
     */
    @Override
    public ResponseEntity<Boolean> createBlog(BlogArticleDTO blogArticleDTO) {

        BlogArticle blogArticle = new BlogArticle(blogArticleDTO);
        blogArticleRepository.save(blogArticle);
        blogArticleRepository.flush();

        return ResponseEntity.ok(Boolean.TRUE);
    }

    /**
     * delete a blog
     * @param token
     * @param username
     * @param id
     * @return
     */
    @Override
    public ResponseEntity<Boolean> deleteBlog(String token, String username, long id) {

        if(!blogArticleRepository.findBlogArticleById(id).getUserName().equals(username))
        {
            return ResponseEntity.internalServerError().build();
        }
        this.commentServiceFeign.deleteAllCommentsByBlogId(id, username);
        blogArticleRepository.deleteById(id);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    /**
     * get all the blog articles that are in the database
     * @return
     */
    @Override
    public ResponseEntity<List<BlogArticleDTO>> getAllBlogArticles()
    {
        List<BlogArticle> allBlogArticles = blogArticleRepository.findAll();
        logger.info("UPDATE BLOG ENTRIES");
        blogArticleRepository.saveAll(allBlogArticles);
        List<BlogArticleDTO> listAsDto =  allBlogArticles.stream().map(BlogArticleDTO::new).collect(toList());
        logger.info("SUCCESSFULLY UPDATED");
        Collections.reverse(listAsDto);
        return ResponseEntity.ok(listAsDto);
    }

    /**
     * get all the blog articles that are in the database for a specific username
     * @return
     */
    @Override
    public ResponseEntity<List<BlogArticleDTO>> getAllBlogArticlesByUserName(String username) {
        List<BlogArticle> allBlogArticles = blogArticleRepository.findBlogArticlesByUserName(username);
        List<BlogArticleDTO> listAsDto =  allBlogArticles.stream().map(BlogArticleDTO::new).collect(toList());
        logger.info("SUCCESSFULLY UPDATED");
        Collections.reverse(listAsDto);
        return ResponseEntity.ok(listAsDto);
    }

    /**
     * was planned to be able to edit blogs
     * @param blogArticleDTO
     * @return
     */
    @Override
    public ResponseEntity<Boolean> editBlog(BlogArticleDTO blogArticleDTO) {
        return null;
    }

    /**
     * get all the blogs that are liked by a specific user
     * @param username
     * @return
     */
    @Override
    public ResponseEntity<List<BlogArticleDTO>> getAllBlogArticlesLikedByUsername(String username) {
        List<Long> listOfLikedBlogIdsByUserId = this.commentServiceFeign.getLikedBlogIdsByUserName(username).getBody();
        assert listOfLikedBlogIdsByUserId != null;
        List<BlogArticle> blogArticles = blogArticleRepository.findAllById(listOfLikedBlogIdsByUserId);
        List<BlogArticleDTO> listToReturn = blogArticles.stream().map(BlogArticleDTO::new).toList();
        return ResponseEntity.ok(listToReturn);
    }

    /**
     * get all the blogs a user commented on
     * @param username
     * @return
     */
    @Override
    public ResponseEntity<List<BlogArticleDTO>> getAllBlogsUserCommentedOn(String username)
    {
        List<Long> listOfLikedBlogIdsByUserId = this.commentServiceFeign.getCommentedBlogIdsByUserComment(username).getBody();
        logger.info("INFO LIST: ");
        logger.info(listOfLikedBlogIdsByUserId.toString());
        List<BlogArticle> blogArticles = blogArticleRepository.findAllById(listOfLikedBlogIdsByUserId);
        List<BlogArticleDTO> listToReturn = blogArticles.stream().map(BlogArticleDTO::new).toList();
        return ResponseEntity.ok(listToReturn);
    }

    /**
     * check if a specific blog exists by id and respective username
     * @param blogId
     * @param username
     * @return
     */
    @Override
    public ResponseEntity<Boolean> doesBlogExistByBlogIdAndUserName(Long blogId, String username) {
        return ResponseEntity.ok(blogArticleRepository.findBlogArticleByIdAndUserName(blogId, username) != null);
    }

    /**
     * get the amount of blogs created by a username
     * @param username
     * @return
     */
    @Override
    public ResponseEntity<Long> getNumberOfBlogsByUserName(String username) {
        return ResponseEntity.ok((long) blogArticleRepository.findBlogArticlesByUserName(username).size());
    }

    /**
     * get all the blogs by their ids and return them
     * @param ids
     * @return
     */
    @Override
    public ResponseEntity<List<BlogArticleDTO>> getAllBlogsById(List<Long> ids) {
        List<BlogArticleDTO> blogArticleDTOs = new ArrayList<>();
        for(Long id : ids)
        {
            BlogArticleDTO blogArticleDTO= new BlogArticleDTO(blogArticleRepository.findBlogArticleById(id));
            blogArticleDTOs.add(blogArticleDTO);
        }
        return ResponseEntity.ok(blogArticleDTOs);
    }


}
