package de.security.microservice.authorizationserver.service;

import de.security.microservice.authorizationserver.model.MyUser;
import de.security.microservice.authorizationserver.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;


//https://www.baeldung.com/spring-security-authentication-with-a-database
//https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/userdetails/UserDetailsService.html

/**
 * This service is there to replace the default userdetailsservice with
 * the default user entity and to load the users that we registered
 * with the entities our own {@link MyUser} class during the authentication process
 */

@Service("my-user-detail-service")
public class MyUserDetailsService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);
    private final UserRepository userRepository;
    private final static Long LOGIN_MAX_ATTEMPT = 2L;

    MyUserDetailsService(UserRepository repository)
    {
        this.userRepository = repository;
    }

    /**
     * Whenever the AuthenticationEventListener calls this
     * function we are increasing the failed counter by one.
     * When the counter hits 3 without having a successful login
     * inbetween the corresponding user is locked out of being able to
     * login for 3 minutes
     * @param username
     */
    public void loginFailed(String username) {
        MyUser user = userRepository.findUserByUsername(username);
        logger.info("USER FAILED ATTEMPTS: " + user.getFailedAttempts());
        if(user.getFailedAttempts() < LOGIN_MAX_ATTEMPT) {
            user.setFailedAttempts(user.getFailedAttempts() + 1L);
        } else {
            logger.info("LOCK " + username + " UNTIL: " + Date.from(Instant.now().plus(Duration.ofMinutes(3L))));
            user.setAccountNonLocked(false);
            user.setLockedUntil(Date.from(Instant.now().plus(Duration.ofMinutes(3L))));
        }
        userRepository.save(user);
    }

    /**
     * this is the function that is responsible to load the UserInformation
     * whenever a Login Attempt is done
     *
     * If the user is null, we are throwing an exception.
     *
     * If the user is is not locked, create an entity of type {@link User}
     * and return it
     *
     * Otherwise throw a {@link AccountLockedException} exception
     * @param username
     * @return {@link UserDetails}
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MyUser foundUser = userRepository.findUserByUsername(username);

        if(foundUser == null)
            throw new UsernameNotFoundException(username + "not found");

        if(foundUser.isAccountNonLocked()) {
            User user = new User(foundUser.getUsername(), foundUser.getPassword(), foundUser.getGrantedAuthoritySet());
            logger.info("LOGGED IN USER: {}", user);
            return user;
        } else {
            if(Date.from(Instant.now()).after(foundUser.getLockedUntil()))
            {
                foundUser.setAccountNonLocked(true);
                foundUser.setFailedAttempts(0L);
                foundUser.setLockedUntil(null);
                userRepository.save(foundUser);
                User user = new User(foundUser.getUsername(), foundUser.getPassword(), foundUser.getGrantedAuthoritySet());
                logger.info("LOGGED IN USER: {}", user);
                return user;
            }

            try {
                throw new AccountLockedException("Account locked until " + foundUser.getLockedUntil());
            } catch (AccountLockedException e) {
                throw new RuntimeException(e);
            }
        }     }

}

