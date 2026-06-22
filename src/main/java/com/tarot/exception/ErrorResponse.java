package com.tarot.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        Map<String, String> fieldErrors
) {

    public ErrorResponse(int status, String error) {
        this(LocalDateTime.now(), status, error, null);
    }

    public ErrorResponse(int status, String error, Map<String, String> fieldErrors) {
        this(LocalDateTime.now(), status, error, fieldErrors);
    }
}
