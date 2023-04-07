package de.security.microservice.userservice.service.UserService;


import de.security.microservice.userservice.model.User.User;
import de.security.microservice.userservice.model.User.dto.UserDTO;

/**
 * interface for the userservice
 */
public interface UserServiceInterface {
    User convertUserDTOintoUser(UserDTO userDTO);

    UserDTO convertUserToUserDTO(User user);

    void registerUser(User user) throws Exception;

    UserDTO getUserByUsername(String username);

    Boolean doesUserExistByUsername(String username);

}
