package de.security.microservice.blogservice.Repository.Model.dto;

import de.security.microservice.blogservice.Repository.Model.BlogArticle;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

/**
 * DTO Object from the {@link BlogArticle} class
 */
@Data()
public class BlogArticleDTO {

    private Long id;

    private String username;

    private String heading;

    private String text;

    private Date creationTimeStamp;

    private Date lastEditedTimeStamp;

    public BlogArticleDTO() {

        this.creationTimeStamp = Date.from(Instant.now());
        this.lastEditedTimeStamp = this.creationTimeStamp;
    }

    public BlogArticleDTO(String username, String heading, String text)
    {
        this.username = username;
        this.heading = heading;
        this.text = text;
        this.creationTimeStamp = Date.from(Instant.now());
        this.lastEditedTimeStamp = this.creationTimeStamp;
    }

    public BlogArticleDTO(BlogArticle blogArticle) {
        if(blogArticle != null) {
            this.id = blogArticle.getId();
            this.username = blogArticle.getUserName();
            this.heading = blogArticle.getHeading();
            this.text = blogArticle.getText();
            this.creationTimeStamp = blogArticle.getCreationTimeStamp();
            this.lastEditedTimeStamp = blogArticle.getLastEditedTimeStamp();
        }
    }



}
