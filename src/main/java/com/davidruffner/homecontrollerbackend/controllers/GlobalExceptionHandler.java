package com.davidruffner.homecontrollerbackend.controllers;

import com.davidruffner.homecontrollerbackend.enums.ResponseCode;
import com.davidruffner.homecontrollerbackend.exceptions.ControllerException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.davidruffner.homecontrollerbackend.enums.ResponseCode.getHttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ControllerException.class)
    public ResponseEntity<ErrorResponse> handleControllerException(
        ControllerException ex,
        HttpServletRequest request
    ) {
        HttpStatus status = getHttpStatus(ex.getResponseCode());

        ErrorResponse.Builder builder = new ErrorResponse.Builder()
            .setErrMsg(ex.getMessage())
            .setStatusCode(ex.getResponseCode().getResponseCodeInt());

        if (ex.getShortCode().isPresent()) {
            builder.setShortCode(ex.getShortCode().get());
        }

        return ResponseEntity
            .status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(builder.build());
    }
}
