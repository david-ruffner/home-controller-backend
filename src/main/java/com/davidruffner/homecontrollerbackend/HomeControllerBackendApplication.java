package com.davidruffner.homecontrollerbackend;

import com.davidruffner.homecontrollerbackend.config.HashUtilConfig;
import com.davidruffner.homecontrollerbackend.config.TodoistConfig;
import com.davidruffner.homecontrollerbackend.config.UserSettingsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    UserSettingsConfig.class,
    HashUtilConfig.class,
    TodoistConfig.class
})
public class HomeControllerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeControllerBackendApplication.class, args);
    }

}
