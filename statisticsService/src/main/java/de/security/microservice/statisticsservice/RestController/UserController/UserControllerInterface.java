package de.security.microservice.statisticsservice.RestController.UserController;


import de.security.microservice.statisticsservice.Model.UserStats.UserStats;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface UserControllerInterface {


    @GetMapping("/getUserStats")
    ResponseEntity<UserStats> getUserStats(String username);
}
