package com.davidruffner.homecontrollerbackend.cache;

import com.davidruffner.homecontrollerbackend.entities.UserSettings;
import com.davidruffner.homecontrollerbackend.repositories.UserSettingsRepository;
import com.davidruffner.homecontrollerbackend.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.davidruffner.homecontrollerbackend.utils.Utils.strNotEmpty;

@Component
public class UserSettingsInterceptor implements HandlerInterceptor {

    private final AuthUtil authUtil;

    public UserSettingsInterceptor(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String authHeader = request.getHeader("Authorization");

        if (strNotEmpty(authHeader) && request.getAttribute(RequestAttrKeys.USER_SETTINGS) == null) {
            UserSettings userSettings = this.authUtil.verifyJWTToken(authHeader);
            request.setAttribute(RequestAttrKeys.USER_SETTINGS, userSettings);
        }

        return true;
    }
}
