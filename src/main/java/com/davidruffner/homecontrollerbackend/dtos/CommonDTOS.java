package com.davidruffner.homecontrollerbackend.dtos;

import java.util.Optional;

public class CommonDTOS {

    public static class CommonResponse {
        private final Optional<String> errMsg;
        private final Boolean status;

        public CommonResponse() {
            this.status = true;
            this.errMsg = Optional.empty();
        }

        public CommonResponse(String errMsg) {
            this.errMsg = Optional.of(errMsg);
            this.status = false;
        }

        public Optional<String> getErrMsg() {
            return errMsg;
        }

        public Boolean getStatus() {
            return status;
        }
    }
}
