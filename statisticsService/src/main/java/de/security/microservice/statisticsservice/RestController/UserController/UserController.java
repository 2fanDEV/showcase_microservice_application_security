package de.security.microservice.statisticsservice.RestController.UserController;

import de.security.microservice.statisticsservice.Model.UserStats.UserStats;
import de.security.microservice.statisticsservice.services.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for the comments for blogs
 *
 * I already knew how to do this, however
 * here is the reference in the documentation
 *
 * Ref:
 * https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller
 *
 * This is a controller for the endpoints of the comment service
 *
 */
@RestController
@RequestMapping("api/services/statistics-service/user")
public class UserController implements UserControllerInterface {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     *  getting the statistics for a user
     * @param username
     * @return
     */
    @Override
    @GetMapping("/getUserStats")
    public ResponseEntity<UserStats> getUserStats(@RequestParam("username") String username)
    {
        return ResponseEntity.ok(userService.userStats(username));
    }




}
