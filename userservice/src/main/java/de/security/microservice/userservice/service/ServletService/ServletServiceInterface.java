package de.security.microservice.userservice.service.ServletService;

import com.nimbusds.jwt.JWT;

import java.text.ParseException;

public interface ServletServiceInterface {

    JWT parseToken(String token) throws ParseException;

    String getUserNameFromToken(String token) throws ParseException;

}
