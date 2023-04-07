package de.security.microservice.commentservice.Repository;

import de.security.microservice.commentservice.model.Comment.Model.Comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * We need an interface that is extending JpaRepository
 * so we are able to write into the database
 *
 * JPA has predefined functions that as far
 * as I know executes database queries like
 *
 * repository.findById()
 *
 * or repository.existsById(id)
 *
 * which do not have to be declared in their own
 * as the class that is being extended already has them
 *
 * visible if you look into the class here
 * {@link JpaRepository}
 *
 * Additionally, you are also able to define your own
 * functions to search for a specific "object"
 * or multiple objects.
 *
 * I always assumed that JPA does create the queries by the name of the functions
 * and apparently the documentation does confirm this:
 *
 * Ref:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.details
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * However, one is also able to create custom queries with the @Query("...") annotation
 * Ref:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-streaming
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentsByUsername(String username);

    List<Comment> findCommentsByBlogId(Long blogId);

    Boolean existsByIdAndUsername(Long id, String username);
}
