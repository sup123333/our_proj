package com.tarot.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends ApiException {

    public DuplicateEmailException(String email) {
        super(HttpStatus.CONFLICT, "Клиент с email " + email + " уже зарегистрирован");
    }
}
