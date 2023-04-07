package de.security.microservice.statisticsservice.Model.compositeKeys;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * I needed a way to create a composite primary key
 * and this baeldung tutorial helped me
 * to do this for spring jpa
 https://www.baeldung.com/spring-jpa-embedded-method-parameters
 */

@Getter
@Setter
@Embeddable
public class CommentKey implements Serializable {

    Long commentId;
    Long blogId;
    String userName;

}
