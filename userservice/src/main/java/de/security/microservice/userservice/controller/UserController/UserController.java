package de.security.microservice.userservice.controller.UserController;


import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import de.security.microservice.userservice.model.User.User;
import de.security.microservice.userservice.model.User.dto.UserDTO;
import de.security.microservice.userservice.service.UserService.UserService;
import de.security.microservice.userservice.service.ServletService.ServletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Controller for the comments for blogs
 *
 * I already knew how to do this, however
 * here is the reference in the documentation
 *
 * Ref:
 * https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller
 *
 * This is a controller class for the endpoints of the userservice
 */
@RestController
@RequestMapping(value = "/api/services/user-service")
public class UserController implements UserControllerInterface {

    Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    private final ServletService servletService;

    /**
     * autowired constructor
     * @param userService
     * @param servletService
     */
    @Autowired
    UserController(UserService userService, ServletService servletService)
    {
        this.userService = userService;
        this.servletService = servletService;
    }

    /**
     * endpoint to register users
     * @param userDTO
     * @return
     */
    @PostMapping("/registerUser")
    @Override
    public ResponseEntity<String> registerAsUser(@RequestBody UserDTO userDTO) {
        System.out.println("\n\n " + userDTO + "\n");
        User user = userService.convertUserDTOintoUser(userDTO);
        userService.registerUser(user);
        return ResponseEntity.ok("User '" + userDTO.getUserName() + "' successfully created");
    }


    /**
     * endpoint to get a specific user
     * @return
     */
    @GetMapping("/getUser")
    @Override
    public ResponseEntity<UserDTO> getUser(HttpServletRequest req)
            throws ParseException {
        logger.info("GET USER");
        String token = req.getHeader("Authorization").replace("Bearer ", "");

        JWT decodedToken = JWTParser.parse(token);
        JWTClaimsSet claims = decodedToken.getJWTClaimsSet();
        String username = claims.getStringClaim("sub");

        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    /**
     * endpoint for checking if a user with the specific
     * username exists
     * @param req
     * @param username
     * @return
     * @throws ParseException
     */
    @PostMapping("/doesUserExist")
    @Override
    public ResponseEntity<Boolean> doesUserExist(HttpServletRequest req, @RequestParam("username") String username)
    throws ParseException {
        logger.info("CALL doesUserExist");
        return ResponseEntity.ok(userService.doesUserExistByUsername(username));
    }

}
