package de.security.microservice.commentservice.controller.CommentController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.security.microservice.commentservice.Repository.CommentRepository;
import de.security.microservice.commentservice.model.Comment.Model.Comment.CommentDto;
import de.security.microservice.commentservice.services.CommentService.CommentService;
import de.security.microservice.commentservice.services.ServletService.ServletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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
@RequestMapping("/api/services/comment-service")
public class CommentController implements CommentControllerInterface{

    Logger logger = LoggerFactory.getLogger(CommentController.class);
    private final CommentRepository commentRepository;

    private final ServletService servletService;

    private final CommentService commentService;


    /**
     * autowire the repository, the servletService and the commentService
     * @param commentRepository
     * @param servletService
     * @param commentService
     */
    @Autowired
    CommentController(CommentRepository commentRepository, ServletService servletService, CommentService commentService)
    {
        this.commentRepository = commentRepository;
        this.servletService = servletService;
        this.commentService = commentService;
    }

    /**
     * endpoint for creating a comment
     * @param servletRequest
     * @param commentDto
     * @return
     * @throws JsonProcessingException
     * @throws ParseException
     */
    @Override
    @PostMapping("/createComment")
    public ResponseEntity<String> createComment(HttpServletRequest servletRequest, @RequestBody CommentDto commentDto)
            throws JsonProcessingException, ParseException {
        String token = servletRequest.getHeader("Authorization").replace("Bearer ", "");
        if (servletService.checkIfUserExists(token) && Objects.equals(servletService.getUserNameFromToken(token), commentDto.getUsername())) {
            commentService.createComment(commentDto);

            return ResponseEntity.ok("Comment created");
        }
        return ResponseEntity.internalServerError().body("The user does not exist");
    }

    /**
     * endpoint for editing a comment which is not used currently
     * @param comment
     * @return
     * @throws Exception
     */
    @Override
    @PostMapping("/editComment")
    public ResponseEntity<String> editComment(String comment) throws
    Exception {
        CommentDto commentDto = getObjectMapper().readValue(comment, CommentDto.class);
        return commentService.editComment(commentDto);
    }

    /**
     * endpoint for deleting comments
     * @param httpServletRequest
     * @param commentId
     * @return
     * @throws ParseException
     */
    @Override
    @PostMapping("/deleteComment")
    public ResponseEntity<String> deleteComment(HttpServletRequest httpServletRequest, Long commentId) throws ParseException {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        if(servletService.checkIfUserExists(token) &&
                commentRepository.findById(commentId) ==
                        servletService.parseToken(token).getJWTClaimsSet().getClaim("username"))
        {
                commentRepository.deleteById(commentId);
                return ResponseEntity.ok("Comment was deleted");
        }
        return ResponseEntity.internalServerError().build();
    }

    /**
     * endpoint for getting comments by their respective ids
     * @param blogId
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/getCommentsByBlogId/{blogId}")
    public ResponseEntity<List<CommentDto>> getCommentsById(@PathVariable Long blogId)
    throws Exception {
        return commentService.getAllCommentsByBlog(blogId);
    }

    /**
     * deleteing all comments based on their blog id
     * @param httpServletRequest
     * @param blogId
     * @return
     * @throws ParseException
     */
    @Override
    @PostMapping("/deleteAllCommentBlogsById")
    public ResponseEntity<Boolean> deleteCommentsByBlogId(HttpServletRequest httpServletRequest,
                                        Long blogId)
    throws ParseException {
        return commentService.deleteAllCommentsByBlogId(blogId);
    }

    /**
     * get all the comments by a respective username
     * @param username
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/getCommentsByUserName")
    public ResponseEntity<List<CommentDto>> getCommentsByUsername(@RequestParam(name = "username") String username) throws Exception {
        return commentService.getAllCommentsByUserName(username);
    }

    /**
     * to read the json objects
     * https://www.baeldung.com/spring-boot-customize-jackson-objectmapper
     * @return
     */
    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    /**
     * return the amount of comments for a specific blog
     * @param blogId
     * @return
     */
    @Override
    @GetMapping("/getCommentAmountForBlogId/{blogId}")
    public ResponseEntity<Long> getCommentAmountByBlogId(@PathVariable Long blogId)
    {
        return commentService.getCommentAmountForBlog(blogId);
    }

    /**
     * get all blog ids a specific user commented on
     * @param username
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/getCommentedBlogIdsByUserComment")
    public ResponseEntity<List<Long>> getCommentedBlogIdsByUserName(@RequestParam(name = "username") String username)
    throws Exception {
        logger.info("get Commented BlogIds By User: " + username);
        List<Long> blogIdsByComment =
                Objects.requireNonNull(commentService.getAllCommentsByUserName(username)
                        .getBody()).stream().map(CommentDto::getBlogId).toList();
        return ResponseEntity.ok(blogIdsByComment);
    }

    /**
     * get the amount of comments for a specific user
     * @param username
     * @return
     */
    @Override
    @GetMapping("/getCommentsSizeByUserName")
    public ResponseEntity<Long> getNumberOfCommentsByUserName(@RequestParam("userName") String username)
    {
        return commentService.getNumberOfCommentsByUserName(username);
    }

    /**
     * check if a comment exists
     * @param commentId
     * @param username
     * @return
     */
    @Override
    @GetMapping("/doesCommentExist")
    public ResponseEntity<Boolean> doesCommentExistByIdAndUserName(Long commentId, String username)
    {
        return commentService.doesCommentExist(commentId, username);
    }

    /**
     * get the amount of comments for a specific blog
     * @param blogIds
     * @return
     */
    @Override
    @PostMapping("/getCommentSizeByBlogId")
    public ResponseEntity<Map<Long, Long>> getCommentSizeByBlogId(@RequestBody List<Long> blogIds)
    {
        return ResponseEntity.ok(commentService.getCommentSizeByBlogId(blogIds));
    }
}
