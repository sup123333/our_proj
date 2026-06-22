package com.tarot.dto.session;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// clientId сознательно отсутствует — он берётся из JWT на стороне контроллера,
// иначе клиент мог бы создать сеанс от имени другого пользователя (IDOR).
public record SessionCreateRequest(

        @NotNull(message = "Услуга обязательна")
        Long serviceId,

        @Size(max = 2000)
        String question,

        boolean usePoints
) {
}
