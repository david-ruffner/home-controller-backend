package com.davidruffner.homecontrollerbackend.exceptions;

import com.davidruffner.homecontrollerbackend.enums.ResponseCode;

import java.util.Optional;

public class ControllerException extends RuntimeException {

    private final ResponseCode responseCode;
    private final Optional<String> shortCode;

    public ControllerException(String message, ResponseCode responseCode) {
        super(message);
        this.responseCode = responseCode;
        this.shortCode = Optional.empty();
    }

    public ControllerException(String message, ResponseCode responseCode, String shortCode) {
        super(message);
        this.responseCode = responseCode;
        this.shortCode = Optional.of(shortCode);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public Optional<String> getShortCode() {
        return shortCode;
    }

    @Override
    public String toString() {
        return super.getMessage();
    }
}
