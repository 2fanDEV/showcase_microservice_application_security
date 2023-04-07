package de.security.microservice.authorizationserver.configuration;

import de.security.microservice.authorizationserver.service.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

//https://spring.io/blog/2015/02/11/better-application-events-in-spring-framework-4-2#annotation-driven-event-listener
//https://docs.spring.io/spring-security/reference/servlet/authentication/events.html
/**
 * Component class that listens for the case that a user
 * has entered bad credentials into the login form
 */
@Component
public class AuthenticationEventListener  {

    Logger logger = LoggerFactory.getLogger(AuthenticationEventListener.class);

    MyUserDetailsService myUserDetailService;

    @Autowired
    AuthenticationEventListener(MyUserDetailsService myUserDetailsService)
    {
        this.myUserDetailService = myUserDetailsService;
    }

    /**
     * EventListener in the case that listens for {@link AuthenticationFailureBadCredentialsEvent}
     * and calls loginFailed from MyUserDetailService and increases the counter
     * of failed logins by one
     * @param auth
     */
    @EventListener
    public void handleAuthenticationFailureEvent(AuthenticationFailureBadCredentialsEvent auth)
    {
        logger.info("FOUND BAD CREDENTIALS -> USERDETAILSERVICE.LOGINFAILURE");
        logger.info(auth.getAuthentication().getPrincipal().toString());
        this.myUserDetailService.loginFailed(auth.getAuthentication().getPrincipal().toString());
    }


}
