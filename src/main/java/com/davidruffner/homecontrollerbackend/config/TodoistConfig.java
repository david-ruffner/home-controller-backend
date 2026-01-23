package com.davidruffner.homecontrollerbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.todoist")
public class TodoistConfig {
    private String apiKey;
    private String apiUrl;

    public TodoistConfig(String apiKey, String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiKeyAsBearer() {
        return "Bearer " + this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
