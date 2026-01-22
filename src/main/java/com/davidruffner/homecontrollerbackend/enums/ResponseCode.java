package com.davidruffner.homecontrollerbackend.enums;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ResponseCode {
    OK(200),
    BAD_REQUEST(400),
    SYSTEM_EXCEPTION(500);

    private int responseCodeInt;

    private static final Map<Integer, ResponseCode> LOOKUP =
        Arrays.stream(values())
            .collect(Collectors.toMap(
                ResponseCode::getResponseCodeInt,
                Function.identity()
            ));

    private static final Map<ResponseCode, HttpStatus> HTTP_STATUS_LOOKUP = Map.of(
        OK, HttpStatus.OK,
        BAD_REQUEST, HttpStatus.BAD_REQUEST,
        SYSTEM_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR
    );

    ResponseCode(int responseCodeInt) {
        this.responseCodeInt = responseCodeInt;
    }

    public int getResponseCodeInt() {
        return responseCodeInt;
    }

    public static Optional<ResponseCode> fromCode(int codeInt) {
        if (LOOKUP.containsKey(codeInt)) {
            return Optional.of(LOOKUP.get(codeInt));
        } else {
            return Optional.empty();
        }
    }

    public static HttpStatus getHttpStatus(ResponseCode responseCode) {
        return HTTP_STATUS_LOOKUP.get(responseCode);
    }
}
