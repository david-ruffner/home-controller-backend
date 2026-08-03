package com.davidruffner.homecontrollerbackend;

import com.davidruffner.homecontrollerbackend.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    UserSettingsConfig.class,
    HashUtilConfig.class,
    TodoistConfig.class,
    FavoriteColorsConfig.class
})
public class HomeControllerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeControllerBackendApplication.class, args);
    }

}
