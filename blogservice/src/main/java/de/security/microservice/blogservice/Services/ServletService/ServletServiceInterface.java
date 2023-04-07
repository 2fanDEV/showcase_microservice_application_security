package de.security.microservice.blogservice.Services.ServletService;

import com.nimbusds.jwt.JWT;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface ServletServiceInterface {

    Boolean getTokenFromRequestAndCheckUser(HttpServletRequest httpServletRequest) throws ParseException;

    JWT parseToken(String token)
            throws ParseException;

    String getUserNameFromToken(String token)
            throws ParseException;
    ResponseEntity<Boolean> checkIfUserExists(String username);
}
