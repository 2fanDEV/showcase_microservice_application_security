package de.security.microservice.commentservice.model.Comment.Model.Comment;

import lombok.Data;

import java.time.Instant;
import java.util.Date;

/**
 * this is a DTO for the Comment class
 */
@Data
public class CommentDto {

    private Long id;

    private String text;

    private String username;

    private Date creationTimeStamp;

    private Date editTimeStamp;

    private Long blogId;
    public CommentDto(Comment comment)
    {
        if(this.id == null)
        {
            this.id = comment.getId();
        }

        this.text = comment.getText();
        this.username = comment.getUsername();
        this.creationTimeStamp = comment.getCreationTimeStamp();
        this.editTimeStamp = comment.getEditTimeStamp();
        this.blogId = comment.getBlogId();
    }

    public CommentDto() {
        this.creationTimeStamp = Date.from(Instant.now());
        this.editTimeStamp = Date.from(Instant.now());
    }


}
