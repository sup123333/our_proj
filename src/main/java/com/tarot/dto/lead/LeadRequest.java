package com.tarot.dto.lead;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Публичный, неаутентифицированный ввод — никаких id клиента/баллов здесь быть не может.
public record LeadRequest(

        @NotBlank(message = "Имя обязательно")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Контакт обязателен")
        @Size(max = 100)
        String contact,

        Long serviceId,

        @Size(max = 2000)
        String question
) {
}
