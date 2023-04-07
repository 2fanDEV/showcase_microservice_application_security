package de.security.microservice.authorizationserver.controller;

import de.security.microservice.authorizationserver.model.Authorities;
import de.security.microservice.authorizationserver.model.MyUser;
import de.security.microservice.authorizationserver.repositories.UserRepository;
import de.security.microservice.authorizationserver.service.RegisterService.RegisterService;
import de.security.microservice.authorizationserver.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * REST Controller for the comments for blogs
 *
 * I already knew how to do this, however
 * here is the reference in the documentation
 *
 * Ref:
 * https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller
 *
 * This is the controller that is responsible for
 * when a person is creating an account
 * on the register form
 */
@RestController
public class RegisterController {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final RegisterService registerService;


    /**
     * Constructor
     * @param userRepository
     * @param bCryptPasswordEncoder
     * @param registerService
     */
    @Autowired
    public RegisterController(UserRepository userRepository,
                              BCryptPasswordEncoder bCryptPasswordEncoder,
                              RegisterService registerService)
    {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.registerService = registerService;
    }

    /**
     * Registering Endpoint
     * that is looking for if a user with the same username is already present
     * and if not, it registers the user and also instantly grants User Authorities
     * except the account name is "admin" for demonstration purposes.
     * @param username
     * @param email
     * @param firstName
     * @param lastName
     * @param password
     * @return {@link ResponseEntity<String>}
     * @throws Exception
     */

    @PostMapping("/registerUser")
    public ResponseEntity<String> registerUser(@RequestParam(value = "username") String username,
                                       @RequestParam(value = "email") String email,
                                       @RequestParam(value = "firstName") String firstName,
                                       @RequestParam(value = "lastName") String lastName,
                                       @RequestParam(value ="password") String password) throws Exception {
        if(userRepository.findUserByUsername(username) != null)
        {
            throw new Exception("User with that username already exists!");
        }
        if (username == null || email == null || firstName == null || lastName == null || password == null) {
            throw new Exception("A field is empty");
        }
        MyUser newlyRegisteredUser = new MyUser();
        newlyRegisteredUser.setUsername(username);
        newlyRegisteredUser.setEmail(email);
        //normally not verified but since i don't have service that can
        // send out emails they get instantly verified
        newlyRegisteredUser.setStatus(Status.VERIFIED);
        newlyRegisteredUser.setFirstName(firstName);
        newlyRegisteredUser.setLastName(lastName);
        newlyRegisteredUser.setEnabled(true);
        newlyRegisteredUser.setAccountNonExpired(true);
        newlyRegisteredUser.setCredentialsNonExpired(true);
        newlyRegisteredUser.setAccountNonLocked(true);
        Set<Authorities> authoritiesSet = new HashSet<>();
        if(username.equals("admin"))
        {
               authoritiesSet.add(new Authorities(username, "ADMIN"));
        } else {
            authoritiesSet.add(new Authorities(username, "USER"));
        }
        newlyRegisteredUser.setAuthorities(authoritiesSet);
        newlyRegisteredUser.setPassword(bCryptPasswordEncoder.encode(password));

        registerService.registerUser(newlyRegisteredUser);


        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://localhost/")).build();
    }
}
