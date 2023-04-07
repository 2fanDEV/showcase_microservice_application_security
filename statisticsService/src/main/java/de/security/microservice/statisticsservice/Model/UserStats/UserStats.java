package de.security.microservice.statisticsservice.Model.UserStats;

import lombok.Data;

@Data
public class UserStats {

    Long receivedLikesForBlogs;

    Long givenLikesForBlogs;

    Long receivedLikesForComments;

    Long givenLikesForComments;

    Long blogsPosted;

    Long commentsPosted;

}
