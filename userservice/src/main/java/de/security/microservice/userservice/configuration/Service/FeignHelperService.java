package de.security.microservice.userservice.configuration.Service;

import de.security.microservice.userservice.configuration.WebMvc.Interceptor.CustomInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;


/**
 * This is a function that is basically just helping
 * the Configuration of each Feign Client such
 * that we are able to save the token we extract in the
 * {@link CustomInterceptor}
 *
 * The only thing needed here was, that
 * the content of the functions were only
 * bound to the current lifecycle
 *
 * and to do that Spring has an annotation which is the {@RequestScope}
 * annotation below, that binds the current state to the request
 *
 * Ref:
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/context/annotation/RequestScope.html
 *
 */
@Component
@RequestScope
public class FeignHelperService {
    Logger logger = LoggerFactory.getLogger(FeignHelperService.class);
    String authorizationHeader;

    public void setAuthorizationHeader(String token)
    {
        if(token != null) {
            logger.info("inserted_token:" + token.replace("Bearer", ""));
            this.authorizationHeader = token;
        }
    }

    public String getAuthorizationHeader()
    {
        return this.authorizationHeader;
    }

}
