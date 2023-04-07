package de.security.microservice.commentservice.services.ServletService;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import de.security.microservice.commentservice.feign.userServiceFeign.UserServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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

    UserServiceFeignClient userServiceFeignClient;

    @Autowired
    ServletService(UserServiceFeignClient userServiceFeignClient)
    {
        this.userServiceFeignClient = userServiceFeignClient;
    }

    /**
     * get the username out of the parsed token
     * and check if the user exists
     * @param req
     * @return
     * @throws ParseException
     */
    @Override
    public JWT parseToken(String token)
    throws ParseException {

        return JWTParser.parse(token);
    }

    /**
     * return a parsed JWT as object
     * @param token
     * @return
     * @throws ParseException
     */
    @Override
    public String getUserNameFromToken(String token)
    throws ParseException {
        JWT jwt = parseToken(token);
        return jwt.getJWTClaimsSet().getStringClaim("sub");
    }

    /**
     * parse the token and return the username
     * @param token
     * @return
     * @throws ParseException
     */
    @Override
    public Boolean checkIfUserExists(String token)
    throws ParseException {
        JWT jwt = parseToken(token);
        String username = jwt.getJWTClaimsSet().getStringClaim("sub");
        logger.info("USERNAME: " + username);
        logger.info("EXTRACTED_TOKEN: " + token);
        return userServiceFeignClient.doesUserExist(token, username).getBody();

    }
}
