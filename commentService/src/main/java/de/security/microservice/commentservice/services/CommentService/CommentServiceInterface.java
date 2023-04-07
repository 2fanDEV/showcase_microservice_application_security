package de.security.microservice.commentservice.services.CommentService;

import de.security.microservice.commentservice.model.Comment.Model.Comment.CommentDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CommentServiceInterface {

    ResponseEntity<String> createComment(CommentDto commentDto);

    ResponseEntity<String> editComment(CommentDto commentDto) throws Exception;

    ResponseEntity<List<CommentDto>> getAllCommentsByBlog(Long blogId) throws Exception;

    ResponseEntity<List<CommentDto>> getAllCommentsByUserName(String username) throws Exception;

    ResponseEntity<Boolean> deleteAllCommentsByBlogId(Long id);

    ResponseEntity<Long> getCommentAmountForBlog(Long blogId);

    ResponseEntity<Long> getNumberOfCommentsByUserName(String userName);

    ResponseEntity<Boolean> doesCommentExist(Long id, String username);

    Map<Long, Long> getCommentSizeByBlogId(List<Long> blogIds);
}
