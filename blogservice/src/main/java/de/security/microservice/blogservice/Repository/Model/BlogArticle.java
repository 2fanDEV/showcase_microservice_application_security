package de.security.microservice.blogservice.Repository.Model;

import de.security.microservice.blogservice.Repository.Model.dto.BlogArticleDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

/**
 * This is the class modeled after
 * the liquibase script in db/changelogs/
 */
@Getter
@Setter
@Table(name = "blogarticle")
@Entity
public class BlogArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private String heading;

    private String text;

    private Date creationTimeStamp;

    private Date lastEditedTimeStamp;

    public BlogArticle(BlogArticleDTO blogDto)
    {
        this.id = blogDto.getId();
        this.userName = blogDto.getUsername();
        this.heading = blogDto.getHeading();
        this.text = blogDto.getText();
        this.creationTimeStamp = blogDto.getCreationTimeStamp();
        this.lastEditedTimeStamp = blogDto.getLastEditedTimeStamp();
    }

    public BlogArticle(String username, String heading, String text)
    {
        this.userName = username;
        this.heading = heading;
        this.text = text;
        this.creationTimeStamp = Date.from(Instant.now());
        this.lastEditedTimeStamp = this.creationTimeStamp;
    }

    public BlogArticle()
    {
        this.creationTimeStamp = Date.from(Instant.now());
        this.lastEditedTimeStamp = this.creationTimeStamp;
    }


}
