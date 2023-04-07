package de.security.microservice.blogservice.Services.ServletService;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import de.security.microservice.blogservice.feign.UserServiceFeign.UserServiceFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
public class ServletService implements ServletServiceInterface {

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
    public Boolean getTokenFromRequestAndCheckUser(HttpServletRequest req)
    throws ParseException {

        String token = req.getHeader("Authorization").replace("Bearer ", "");
        logger.info(token);
        JWT decodedToken = JWTParser.parse(token);
        JWTClaimsSet claims = decodedToken.getJWTClaimsSet();
        String username = claims.getStringClaim("sub");
        logger.info("GET USER " + username + " FROM USERSERVICE");
        return this.checkIfUserExists(username).getBody();
    }

    /**
     * return a parsed JWT as object
     * @param token
     * @return
     * @throws ParseException
     */
    @Override
    public JWT parseToken(String token)
            throws ParseException {

        return JWTParser.parse(token);
    }

    /**
     * parse the token and return the username
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
     * check with a request towards the user service if a specific user exists
     * @param username
     * @return
     */
    @Override
    public ResponseEntity<Boolean> checkIfUserExists(String username) {
        return userServiceFeignClient.doesUserExist(username);
    }


}
