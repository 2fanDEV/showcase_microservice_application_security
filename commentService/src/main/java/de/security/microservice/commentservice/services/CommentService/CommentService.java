package de.security.microservice.commentservice.services.CommentService;

import de.security.microservice.commentservice.Repository.CommentRepository;
import de.security.microservice.commentservice.feign.blogServiceFeign.BlogServiceFeignClient;
import de.security.microservice.commentservice.feign.userServiceFeign.UserServiceFeignClient;
import de.security.microservice.commentservice.model.Comment.Model.Comment.Comment;
import de.security.microservice.commentservice.model.Comment.Model.Comment.CommentDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

/**
 * The @Service annotation makes this service
 * enabled for the @Autowired annotation, so we can inject
 * this service in the constructor of a Controller for example
 *
 * Ref:
 * https://docs.spring.io/spring-ws/docs/current/reference/html/#_providing_the_service_and_stub_implementation
 */
@Service
public class CommentService implements CommentServiceInterface {

    private final CommentRepository commentRepository;

    private final UserServiceFeignClient userServiceFeignClient;

    private final BlogServiceFeignClient blogServiceFeignClient;
    Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    CommentService(CommentRepository commentRepository,
                   BlogServiceFeignClient blogServiceFeignClient,
                   UserServiceFeignClient userServiceFeignClient)
    {
        this.commentRepository = commentRepository;
        this.userServiceFeignClient = userServiceFeignClient;
        this.blogServiceFeignClient = blogServiceFeignClient;
    }

    /**
     * creating a comment
     * @param commentDto
     * @return
     */
    @Override
    public ResponseEntity<String> createComment(CommentDto commentDto) {
        Comment comment = new Comment(commentDto);
        this.commentRepository.save(comment);
        logger.info("COMMENT CREATED: " + commentDto);
        return ResponseEntity.ok("Comment created");
    }

    /**
     * editing a comment
     * @param commentDto
     * @return
     * @throws Exception
     */
    @Override
    public ResponseEntity<String> editComment(CommentDto commentDto)
    throws Exception {
        Comment commentToBeChanged = commentRepository.findById(commentDto.getId()).orElse(null);

        assert commentToBeChanged != null;

        if(!Objects.equals(commentDto.getUsername(), commentToBeChanged.getUsername()))
        {
            logger.error("Comment does not exist, or wrong username");
            throw new Exception("Comment doesn't exist!");
        }

        commentToBeChanged = new Comment(commentDto);

        commentRepository.save(commentToBeChanged);
        return ResponseEntity.ok("Comment edited!");
    }

    /**
     * getting all the comments for a specific blog by id
     * @param blogId
     * @return
     * @throws Exception
     */
    @Override
    public ResponseEntity<List<CommentDto>> getAllCommentsByBlog(Long blogId)
    throws Exception {
        List<Comment> commentList = commentRepository.findCommentsByBlogId(blogId);
        Collections.reverse(commentList);
        List<CommentDto> commentDtoList = commentList.stream().map(CommentDto::new).toList();
        if(commentList.size() == commentDtoList.size())
            return ResponseEntity.ok(commentDtoList);
        else
            throw new Exception("Error while retrieving all comments for blog");
    }

    /**
     * get all the comments by an user name
     * @param username
     * @return
     * @throws Exception
     */
    @Override
    public ResponseEntity<List<CommentDto>> getAllCommentsByUserName(String username)
    throws Exception {
        List<Comment> allCommentsByUserName = commentRepository.findCommentsByUsername(username);
        List<CommentDto> convertedToCommentDtoList = allCommentsByUserName.stream().map(CommentDto::new).toList();
        logger.info("INFO LIST: ");
        logger.info(convertedToCommentDtoList.toString());
        if(convertedToCommentDtoList.size() == allCommentsByUserName.size())
        {
            return ResponseEntity.ok(convertedToCommentDtoList);
        } else
                throw new Exception("Error retrieving all comments by username");


    }


    /**
     * this function is only called when a blogarticle
     * gets deleted completely, thus we remove all comments in the db associated with that blog and also remove every like for the statistics
     * @param id
     * @return
     */
    @Override
    public ResponseEntity<Boolean> deleteAllCommentsByBlogId(Long id)
    {

        List<Comment> allCommentsByBlogId = commentRepository.findCommentsByBlogId(id);
        for(Comment comment : allCommentsByBlogId)
        {
            commentRepository.deleteById(comment.getId());
        }


        return ResponseEntity.ok(Boolean.TRUE);
    }

    /**
     * get the amount of comments for a blog
     * @param blogId
     * @return
     */
    @Override
    public ResponseEntity<Long> getCommentAmountForBlog(Long blogId)
    {
        return ResponseEntity.ok((long) commentRepository.findCommentsByBlogId(blogId).size());
    }

    /**
     * get the number of comments a user has created
     * @param userName
     * @return
     */
    @Override
    public ResponseEntity<Long> getNumberOfCommentsByUserName(String userName)
    {
        return ResponseEntity.ok((long) commentRepository.findCommentsByUsername(userName).size());
    }

    /**
     * check in repository if comment exists
     * @param id
     * @param username
     * @return
     */
    @Override
    public ResponseEntity<Boolean> doesCommentExist(Long id, String username)
    {
        return ResponseEntity.ok(commentRepository.existsByIdAndUsername(id, username));
    }

    /**
     * get the size of comment for a list of blogs by their ids
     * @param blogIds
     * @return
     */
    @Override
    public Map<Long, Long> getCommentSizeByBlogId(List<Long> blogIds) {
        Map<Long, Long> mapEntries = new HashMap<>();
        for(Long blogId : blogIds)
        {
            mapEntries.put(blogId, (long) commentRepository.findCommentsByBlogId(blogId).size());
        }
        return mapEntries;
    }
}
