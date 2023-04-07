package de.security.microservice.statisticsservice.services.CommentService;

import java.util.List;
import java.util.Map;

public interface CommentServiceInterface {


    void likeComment(String userName, String authorName, Long commentId, Long blogId) throws Exception;

    Map<Long, Long> likesForCommentIds(List<Long> commentIds);

    List<Long> likesForCommentByUserName(String username);
}
