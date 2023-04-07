package de.security.microservice.userservice.controller.UserController;

import de.security.microservice.userservice.model.User.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface UserControllerInterface {


    ResponseEntity<String> registerAsUser(UserDTO userDTO);

    ResponseEntity<UserDTO> getUser(HttpServletRequest req) throws ParseException;

    @PostMapping("/doesUserExist")
    ResponseEntity<Boolean> doesUserExist(HttpServletRequest req, @RequestParam("username") String username) throws ParseException;

}
