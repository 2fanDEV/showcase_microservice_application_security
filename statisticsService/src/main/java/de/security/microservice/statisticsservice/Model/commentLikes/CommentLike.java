package de.security.microservice.statisticsservice.Model.commentLikes;

import de.security.microservice.statisticsservice.Model.compositeKeys.CommentKey;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Date;

/**
 *  As a composite key was needed as a primary key,
 *  I needed to declare a way to display it as a class for JPA.
 *
 *  Otherwise an exception was thrown, that the class was not being able
 *  to be found
 *
 *  https://www.baeldung.com/spring-jpa-embedded-method-parameters
 */
@Getter
@Setter
@Entity
public class CommentLike {

    @EmbeddedId
    CommentKey commentKey;

    String authorName;

    Date likeTimeStamp;

}
