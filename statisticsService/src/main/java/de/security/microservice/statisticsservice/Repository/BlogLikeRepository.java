package de.security.microservice.statisticsservice.Repository;

import de.security.microservice.statisticsservice.Model.blogLike.BlogLike;
import de.security.microservice.statisticsservice.Model.compositeKeys.BlogKey;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
 *
 * For the SQL queries with "fetch first" I had too look up on how to do this.
 * Ref: https://www.w3schools.com/sql/sql_top.asp
 *
 * For the named @Param("..") another section of the
 * documentation was looked up:
 *
 * Ref:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.named-parameters
 */
@Repository
public interface BlogLikeRepository extends JpaRepository<BlogLike, BlogKey> {

    @Query(value = "SELECT * FROM blog_like WHERE user_name = :username fetch first 1 rows only", nativeQuery = true)
    BlogLike findFirstByUserName(@Param("username") String username);

    BlogLike findFirstByAuthorName(String authorName);

    @Query(value = "SELECT * FROM blog_like WHERE blog_id = :blogid fetch first 1 rows only", nativeQuery = true)
    BlogLike findFirstByBlogId(@Param("blogid") Long blogid);

    @Query(value = "SELECT * FROM blog_like WHERE blog_id = :blogid", nativeQuery = true)
    List<BlogLike> findBlogLikesByBlogId(@Param("blogid") Long blogid);
    @Query(value = "SELECT * FROM blog_like WHERE user_name = :username", nativeQuery = true)
    List<BlogLike> findBlogLikesByUserName(@Param("username") String username);
    List<BlogLike> findBlogLikesByAuthorName(String authorName);

}
