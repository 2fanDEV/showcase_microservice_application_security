package de.security.microservice.authorizationserver.service.RegisterService;

import de.security.microservice.authorizationserver.model.MyUser;
import de.security.microservice.authorizationserver.model.UserDTO;


public interface RegisterServiceInterface {

    void registerUser(MyUser myUser) throws Exception;
    UserDTO convertMyUserToUserFeignDto(MyUser myUser);
}
