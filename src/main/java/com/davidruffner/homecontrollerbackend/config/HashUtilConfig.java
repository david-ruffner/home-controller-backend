package com.davidruffner.homecontrollerbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.hash-util")
public class HashUtilConfig {
    private String digestType;
    private String jwtKey;

    public HashUtilConfig(String digestType, String jwtKey) {
        this.digestType = digestType;
        this.jwtKey = jwtKey;
    }

    public String getDigestType() {
        return digestType;
    }

    public void setDigestType(String digestType) {
        this.digestType = digestType;
    }

    public String getJwtKey() {
        return jwtKey;
    }

    public void setJwtKey(String jwtKey) {
        this.jwtKey = jwtKey;
    }
}
