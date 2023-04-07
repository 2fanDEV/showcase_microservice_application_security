package de.security.microservice.authorizationserver.controller;

import de.security.microservice.authorizationserver.repositories.UserRepository;
import de.security.microservice.authorizationserver.service.ServletService.ServletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * A RestController that is there for CRUD Operations
 * for getting informations about specific users or
 * change user data like the user or look up if the user exists
 *
 *  I already knew how to do this, however
 *  here is the reference in the documentation
 *
 *  Ref:
 *  https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller
 */
@RestController
@RequestMapping(value = "/api/services/auth-service")
public class UserController {
    UserRepository userRepository;

    ServletService servletService;

    @Autowired
    UserController(UserRepository userRepository, ServletService servletService)
    {
        this.userRepository = userRepository;
    }

    /**
     * checks if a user with said username exists
     * @param username
     * @param token
     * @return
     */
    @PostMapping("/getUser")
    public Boolean doesUserExist(@RequestParam("username") String username, String token)
    {
        return userRepository.existsByUsername(username);
    }

}
