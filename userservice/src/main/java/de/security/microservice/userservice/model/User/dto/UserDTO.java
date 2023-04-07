package de.security.microservice.userservice.model.User.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.core.lang.Nullable;
import lombok.Data;

import javax.validation.constraints.Email;
import java.util.Date;


/**
 * Data Transfer Object for User Entity
 */
@Data
public class UserDTO {

    private String userName;

    private String firstName;

    private String lastName;

    @Email
    private String email;

    private String birthday;

}
