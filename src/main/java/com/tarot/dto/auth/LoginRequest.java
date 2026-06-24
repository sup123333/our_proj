package com.tarot.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Укажи телефон или telegram")
        String contact,

        @NotBlank(message = "Пароль обязателен")
        String password
) {
}
