package de.security.microservice.api_gateway.routing.Controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * This is a RestController
 * for the authorized endpoint
 * at the authorization server, so we are able to retrieve
 * the generated JWT Tokens
 *
 * as through the Router class it was not possible to achieve the wanted functionality,
 * i tried to do a manual routing mechanism to the authorization server which worked.
 *
 *  Ref:
 *  https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller
 *
 *  This is a controller for the endpoints of the comment service
 */
@RestController
public class ManualRouter {

    @GetMapping("/api/v0/auth-service/authorized")
    public ResponseEntity<String> manualRouting(@RequestParam("code") String code) {
        String buildUrl = "https://localhost/authorized?code=";
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(buildUrl + code)).build();
    }

}
