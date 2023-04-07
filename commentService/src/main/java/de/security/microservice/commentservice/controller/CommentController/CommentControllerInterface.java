package de.security.microservice.commentservice.controller.CommentController;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.security.microservice.commentservice.model.Comment.Model.Comment.CommentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface CommentControllerInterface {

    @PostMapping("/createComment")
    ResponseEntity<String> createComment(HttpServletRequest httpServletRequest, @RequestBody CommentDto comment) throws JsonProcessingException, ParseException;

    @PostMapping("/editComment")
    ResponseEntity<String> editComment(String comment) throws Exception;

    @PostMapping("/deleteComment")
    ResponseEntity<String> deleteComment(HttpServletRequest httpServletRequest, Long commentId) throws ParseException;

    @GetMapping("/getCommentsById/{blogId}")
    ResponseEntity<List<CommentDto>> getCommentsById(@PathVariable Long blogId) throws Exception;

    @PostMapping("/deleteCommentsByBlogId")
    ResponseEntity<Boolean> deleteCommentsByBlogId(HttpServletRequest httpServletRequest, @RequestParam(name = "blogId" )Long blogId) throws ParseException;

    @GetMapping("/getCommentsByUserName")
    ResponseEntity<List<CommentDto>> getCommentsByUsername(@RequestParam String username) throws Exception;

    @GetMapping("/getCommentAmountForBlogId/{blogId}")
    ResponseEntity<Long> getCommentAmountByBlogId(@PathVariable Long blogId);

    @GetMapping("/getCommentedBlogIdsByUserComment")
    ResponseEntity<List<Long>> getCommentedBlogIdsByUserName(@RequestParam(name = "username") String username) throws Exception;

    @GetMapping("/getCommentsSizeByUserName")
    ResponseEntity<Long> getNumberOfCommentsByUserName(String username);

    @GetMapping("/doesCommentExist")
    ResponseEntity<Boolean> doesCommentExistByIdAndUserName(Long commentId, String username);

    @GetMapping("/getCommentSizeByBlogId")
    ResponseEntity<Map<Long, Long>> getCommentSizeByBlogId(List<Long> blogIds);
}
