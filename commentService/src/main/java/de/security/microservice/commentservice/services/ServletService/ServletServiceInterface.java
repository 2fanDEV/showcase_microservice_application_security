package de.security.microservice.commentservice.services.ServletService;

import com.nimbusds.jwt.JWT;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public interface ServletServiceInterface {

    JWT parseToken(String token) throws ParseException;

    String getUserNameFromToken(String token) throws ParseException;

    Boolean checkIfUserExists(String token) throws ParseException;
}
