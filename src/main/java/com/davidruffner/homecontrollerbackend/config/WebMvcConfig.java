package com.davidruffner.homecontrollerbackend.config;

import com.davidruffner.homecontrollerbackend.cache.UserSettingsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserSettingsInterceptor userSettingsInterceptor;

    public WebMvcConfig(UserSettingsInterceptor userSettingsInterceptor) {
        this.userSettingsInterceptor = userSettingsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userSettingsInterceptor);
    }
}
