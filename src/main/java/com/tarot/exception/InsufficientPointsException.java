package com.tarot.exception;

import org.springframework.http.HttpStatus;

public class InsufficientPointsException extends ApiException {

    public InsufficientPointsException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
