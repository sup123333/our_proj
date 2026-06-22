package com.tarot.dto.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceRequest(

        @NotBlank(message = "Название обязательно")
        @Size(max = 150)
        String name,

        @Size(max = 2000)
        String description,

        @NotNull(message = "Цена обязательна")
        @DecimalMin(value = "0.01", message = "Цена должна быть положительной")
        BigDecimal price,

        @PositiveOrZero(message = "Баллы за услугу не могут быть отрицательными")
        Integer pointsReward,

        Boolean active
) {
}
