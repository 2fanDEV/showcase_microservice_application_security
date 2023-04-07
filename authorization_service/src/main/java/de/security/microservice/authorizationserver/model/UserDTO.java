package de.security.microservice.authorizationserver.model;

import lombok.Data;
import java.util.Date;

/**
 * A dto class which is only populated
 * when sending requests to other services as an answer about
 * the user entity such that the "locked until"
 * or "the password" is not included in requests
 */
@Data
public class UserDTO {

    private Long id;

    private String userName;

    private String firstName;

    private String lastName;

    private String email;

    private String birthday;
}
