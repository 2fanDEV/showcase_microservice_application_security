package de.security.microservice.userservice.service.UserService;

import de.security.microservice.userservice.feign.AuthServiceFeign;
import de.security.microservice.userservice.model.User.User;
import de.security.microservice.userservice.model.User.dto.UserDTO;
import de.security.microservice.userservice.repositories.UserRepository;
import de.security.microservice.userservice.service.ServletService.ServletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * This service handles everything that
 * is related to users like converting User Objects
 * into DTOs, registering users, looking up if a specific user exists in
 * the database
 */
@Service
public class UserService implements UserServiceInterface {
        Logger logger = LoggerFactory.getLogger(UserService.class);
        private final UserRepository userRepository;

        private final ServletService servletService;

        private final AuthServiceFeign authServerFeign;

        @Autowired
        UserService(UserRepository userRepository,
                    ServletService servletService, AuthServiceFeign authServiceFeign)
        {
            this.userRepository = userRepository;
            this.servletService = servletService;
            this.authServerFeign = authServiceFeign;
        }

    /**
     * when getting a userDto from the frontend we are going to
     * convert it to a regular User DataType since these have little
     * additional information we don't need at the frontend
     * @param userDTO {@link UserDTO}
     * @return user {@link User}
     */
    @Override
    public User convertUserDTOintoUser(UserDTO userDTO)
    {
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthday("14.11.1997");
        user.setEmail(userDTO.getEmail());
        return user;
    }

    /**
     * when we want to send a user to the frontend or somewhere
     * else the additional information we probably could have is not necessary.
     * @param user {@link User user}
     * @return userDTO {@link UserDTO}
     */
    @Override
    public UserDTO convertUserToUserDTO(User user)
    {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setBirthday(user.getBirthday());
        return userDTO;
    }

    /**
     *
     * @param user
     * @throws Exception
     */
    @Override
    public void registerUser(User user) throws DuplicateKeyException {
        user.setAccountCreationDate(Date.from(Instant.now()));
        user.setStatus("VERIFIED");
        if(!userRepository.existsByUserName(user.getUserName()))
        {
            userRepository.save(user);
            userRepository.flush();
        } else {
            throw new DuplicateKeyException("user already exists!");
        }
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUserName(username);
        return convertUserToUserDTO(user);
    }

    @Override
    public Boolean doesUserExistByUsername(String username)
    {
        return userRepository.existsByUserName(username);
    }

}
