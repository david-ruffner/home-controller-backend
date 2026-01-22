package com.davidruffner.homecontrollerbackend.config;

import com.davidruffner.homecontrollerbackend.enums.AccountType;
import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "app.user-settings")
public class UserSettingsConfig {
    private Map<String, List<String>> allowedApps;

    public UserSettingsConfig(Map<String, List<String>> allowedApps) {
        this.allowedApps = allowedApps;
    }

    public Map<String, List<String>> getAllowedApps() {
        return allowedApps;
    }

    public void setAllowedApps(Map<String, List<String>> allowedApps) {
        this.allowedApps = allowedApps;
    }

    public List<String> getAllowedAppsByAccountType(AccountType accountType) {
        if (!this.allowedApps.containsKey(accountType.getValue())) {
            throw new ControllerException(String.format("Tried to get allowed apps for a " +
                "non-existent user type '%s'", accountType), ResponseCode.SYSTEM_EXCEPTION);
        }

        return this.allowedApps.get(accountType.getValue());
    }
}
