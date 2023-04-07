package de.security.microservice.statisticsservice.services.BlogService;

import org.springframework.http.ResponseEntity;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Map;

public interface BlogServiceInterface {

    ResponseEntity<String> likeBlog(String username, String authorName, Long blogId) throws Exception;

    List<Long> likesForBlogByUserName(String username);

    Map<Long, Long> likesForBlogIds(List<Long> blogIds);
}
