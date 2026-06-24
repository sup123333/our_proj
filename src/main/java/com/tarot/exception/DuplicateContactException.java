package com.tarot.exception;

import org.springframework.http.HttpStatus;

public class DuplicateContactException extends ApiException {

    public DuplicateContactException(String contact) {
        super(HttpStatus.CONFLICT, "Клиент с контактом " + contact + " уже зарегистрирован");
    }
}
