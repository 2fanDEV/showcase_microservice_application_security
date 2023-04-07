package de.security.microservice.authorizationserver.service.ServletService;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;

/**
 * A Service that is responsible to parse
 * the token and also get the username out of the JWT Token
 *
 * I was trying to find out why I was not able to
 * create an Object of Type {@link JWTParser},
 * but if you are looking into the class, then
 * you can see that there is only a static function
 * that returns {@link JWT}, which
 * means that this function can be
 * used without creating an instance of it
 *
 * To further confirm this, here is a documentation link:
 * https://www.javadoc.io/doc/com.nimbusds/nimbus-jose-jwt/4.4/com/nimbusds/jwt/JWTParser.html
 */
@Service
public class ServletService implements ServletServiceInterface{

    Logger logger = LoggerFactory.getLogger(ServletService.class);

    @Override
    public JWT parseToken(String token)
    throws ParseException {

        return JWTParser.parse(token);
    }

    @Override
    public String getUserNameFromToken(String token)
    throws ParseException {
        JWT jwt = parseToken(token);
        return jwt.getJWTClaimsSet().getStringClaim("sub");
    }


}
