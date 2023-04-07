package de.security.microservice.commentservice.model.Comment.Model.Comment;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;


/**
 * This is a class based on
 * the liquibase script in /resources/db/changelog
 * such that JPA is able to retrieve the
 * comments from the database
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @Column(name = "username")
    private String username;

    private Date creationTimeStamp;

    private Date editTimeStamp;

    private Long blogId;

    public Comment() {}

    Comment(String text,
            String username,
            Date creationTimeStamp,
            Date editTimeStamp,
            Long blogId, Long likes)
    {
        this.text = text;
        this.username = username;
        this.creationTimeStamp = Date.from(Instant.now());
        this.editTimeStamp = Date.from(Instant.now());
        this.blogId = blogId;
    }

    public Comment(CommentDto commentDto)
    {
        if(commentDto.getId() != null)
        {
            this.id = commentDto.getId();
        }
        this.text = commentDto.getText();
        this.username = commentDto.getUsername();
        this.creationTimeStamp = commentDto.getCreationTimeStamp();
        this.editTimeStamp = commentDto.getEditTimeStamp();
        this.blogId = commentDto.getBlogId();
    }
}
