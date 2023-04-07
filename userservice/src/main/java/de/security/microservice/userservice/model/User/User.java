package de.security.microservice.userservice.model.User;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * Class for User
 */
@Getter
@Setter
@Table(name = "users")
@ToString
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "username cannot be null")
    private String userName;

    @NotNull(message = "First Name can not be null")
    private String firstName;

    @NotNull(message = "Last Name can not be null")
    private String lastName;

    @NotNull(message = "Status cannot be null")
    private String status;

    @NotNull(message = "Email can not be null")
    @Email
    private String email;

    @NotNull(message = "Birthday can not be null")
    private String birthday;

    @NotNull(message = "Creation Date")
    private Date accountCreationDate;


}
