package de.security.microservice.authorizationserver.service.RegisterService;

import de.security.microservice.authorizationserver.model.MyUser;
import de.security.microservice.authorizationserver.repositories.UserRepository;
import de.security.microservice.authorizationserver.feign.UserServiceFeign;
import de.security.microservice.authorizationserver.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The @Service annotation makes this service
 * enabled for the @Autowired annotation, so we can inject
 * this service in the constructor of a Controller for example
 *
 * Ref:
 * https://docs.spring.io/spring-ws/docs/current/reference/html/#_providing_the_service_and_stub_implementation
 */
@Service
public class RegisterService implements  RegisterServiceInterface{

    private final UserRepository userRepository;

    private final UserServiceFeign userServiceFeign;

    /**
     * Constructor
     * @param userRepository
     * @param userServiceFeign
     */
    @Autowired
    RegisterService(UserRepository userRepository, UserServiceFeign userServiceFeign)
    {
        this.userRepository = userRepository;
        this.userServiceFeign = userServiceFeign;
    }


    /**
     * responsible for saving created user
     * to the db
     * @param myUser
     * @throws Exception
     */
    @Override
    public void registerUser(MyUser myUser)
    throws Exception {
        if(!userRepository.existsByUsername(myUser.getUsername())) {
            UserDTO user = this.convertMyUserToUserFeignDto(myUser);
            try {
                userServiceFeign.registerUser(user);
            } catch (Exception e)
            {
                System.out.println(e.getMessage());
                throw new Exception("Couldn't send to user-service");
            }
            myUser.setFailedAttempts(0L);
            userRepository.save(myUser);
        }
    }



    /**
     * we are converting the information inside of MyUser to UserDTO
     * since the UserService is expecting a different entity with less columns/properties
     * @param myUser
     * @return
     */
    @Override
    public UserDTO convertMyUserToUserFeignDto(MyUser myUser)
    {
        UserDTO userFeignUserDto = new UserDTO();
        userFeignUserDto.setUserName(myUser.getUsername());
        userFeignUserDto.setEmail(myUser.getEmail());
        userFeignUserDto.setFirstName(myUser.getFirstName());
        userFeignUserDto.setLastName(myUser.getLastName());
        userFeignUserDto.setBirthday(null);
        return userFeignUserDto;
    }
}
