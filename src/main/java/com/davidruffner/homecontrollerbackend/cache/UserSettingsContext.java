package com.davidruffner.homecontrollerbackend.cache;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.enums.ShortCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static com.davidruffner.homecontrollerbackend.enums.ResponseCode.SYSTEM_EXCEPTION;

@Component
public class UserSettingsContext {

    public UserSettings getRequired() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new ControllerException("Was required to fetch UserSettings " +
                "while there were no active HTTP requests.", SYSTEM_EXCEPTION,
                ShortCode.SYSTEM_EXCEPTION.toString());
        }

        HttpServletRequest request = attrs.getRequest();
        UserSettings userSettings = (UserSettings) request.getAttribute(RequestAttrKeys.USER_SETTINGS);
        if (userSettings == null) {
            throw new ControllerException("Was required to fetch UserSettings while there were no" +
                " active HTTP requests.", SYSTEM_EXCEPTION, ShortCode.SYSTEM_EXCEPTION.toString());
        }

        return userSettings;
    }

    public Optional<UserSettings> getOptional() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return Optional.empty();

        return Optional.of((UserSettings) attrs.getRequest().getAttribute(RequestAttrKeys.USER_SETTINGS));
    }
}
