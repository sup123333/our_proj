package com.tarot.dto.session;

import jakarta.validation.constraints.Size;

// clientId сознательно отсутствует — он берётся из JWT на стороне контроллера,
// иначе клиент мог бы создать сеанс от имени другого пользователя (IDOR).
// serviceId и questionCount — взаимоисключающие способы задать услугу: serviceId для тем
// из подобранных карточек, questionCount — для своего вопроса (ownQuestion=true). Их согласованность
// проверяется в сервисном слое, а не аннотациями — там же лучше видна бизнес-логика выбора.
public record SessionCreateRequest(

        Long serviceId,

        Integer questionCount,

        @Size(max = 2000)
        String question,

        boolean usePoints,

        // true — свой вопрос вместо темы из подобранных карточек, без скидки за объём
        boolean ownQuestion
) {
}
