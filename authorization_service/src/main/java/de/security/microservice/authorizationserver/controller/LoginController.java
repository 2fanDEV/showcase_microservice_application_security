package de.security.microservice.authorizationserver.controller;

import de.security.microservice.authorizationserver.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;


/**
 * basic controller that is responsible
 * for login/logout and registering new users
 *
 * Thymeleaf with Spring does then bring the login page to the user:
 * Ref: https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
 */
@Controller
public class LoginController {

    Logger logger = LoggerFactory.getLogger(LoginController.class);
    UserRepository userRepository;

    @Autowired
    public LoginController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    /**
     * Pulls the login.xml template and
     * shows the site where
     * users enter the credentials
     * @param httpServletRequest
     * @param model
     * @param error
     * @return
     */
    @GetMapping("/login")
    public String login(HttpServletRequest httpServletRequest,
                        Model model, @RequestParam(name = "error", required = false) String error) {
        if(error != null) {
            model.addAttribute("error", "Wrong password or Account is locked for 3 Minutes");
        }
        return "login";
    }


    /**
     * returns the register.xml formular
     * where users can create an account
     * @return
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * logs the user out and invalidates the token
     * and any session that may have been created
     * @param request
     * @return
     * @throws ServletException
     */
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request)
    throws ServletException {
            request.getSession(true).invalidate();
            request.logout();
           return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("https://localhost/")).build();
    }

}
