package com.davidruffner.homecontrollerbackend.controllers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public final class ErrorResponse {
    private final Instant timestamp;
    private final int statusCode;
    private final String shortCode;
    private final String errMsg;
    private final Map<String, Object> details;

    private ErrorResponse(Builder builder) {
        this.timestamp = builder.timestamp;
        this.statusCode = builder.statusCode;
        this.shortCode = builder.shortCode;
        this.errMsg = builder.errMsg;
        this.details = builder.details;
    }

    public static final class Builder {
        private Instant timestamp;
        private int statusCode;
        private String shortCode;
        private String errMsg;
        private Map<String, Object> details = new HashMap<>();

        public Builder() {
            this.timestamp = Instant.now();
        }

        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder setShortCode(String shortCode) {
            this.shortCode = shortCode;
            return this;
        }

        public Builder setErrMsg(String errMsg) {
            this.errMsg = errMsg;
            return this;
        }

        public Builder setDetails(Map<String, Object> details) {
            this.details = details;
            return this;
        }

        public Builder addDetails(String key, Object value) {
            this.details.put(key, value);
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
