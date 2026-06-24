package com.tarot.config;

import com.tarot.entity.Service;
import com.tarot.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

// Лендинг показывает фиксированный набор услуг, для которых форма записи должна получить
// реальные serviceId. Каждая услуга добавляется по имени независимо от того, пуста ли таблица —
// иначе пакеты, добавленные в каталог позже, никогда не появятся на уже работающей базе.
@Component
@RequiredArgsConstructor
public class ServiceCatalogSeeder implements CommandLineRunner {

    private final ServiceRepository serviceRepository;

    @Override
    public void run(String... args) {
        List.of(
                Service.builder().name("Консультация (40 минут)")
                        .description("Личный разговор без расклада — отвечаю на вопросы голосом или в переписке.")
                        .price(new BigDecimal("500.00")).pointsReward(10).active(true).build(),
                Service.builder().name("Расклад на 1 вопрос")
                        .description("Один вопрос — развёрнутый ответ. Когда застряла и не видишь выхода.")
                        .price(new BigDecimal("200.00")).pointsReward(10).active(true).build(),
                Service.builder().name("Расклад на 2 вопроса")
                        .description("Два вопроса в одном сеансе — разберём каждый по отдельности.")
                        .price(new BigDecimal("350.00")).pointsReward(10).active(true).build(),
                Service.builder().name("Расклад на 3 вопроса")
                        .description("Три вопроса в одном сеансе — самый полный разбор твоей ситуации.")
                        .price(new BigDecimal("500.00")).pointsReward(10).active(true).build(),
                Service.builder().name("Расклад на 5 вопросов")
                        .description("Пакет из пяти вопросов — выгоднее, чем по одному. Действует скидка за объём на темах с карточек.")
                        .price(new BigDecimal("1000.00")).pointsReward(20).active(true).build(),
                Service.builder().name("Расклад на 7 вопросов")
                        .description("Большой разбор на семь вопросов — максимальная скидка за объём на темах с карточек.")
                        .price(new BigDecimal("1500.00")).pointsReward(30).active(true).build(),
                Service.builder().name("Матрица судьбы — любой 1 личный вопрос")
                        .description("Разбор твоей матрицы судьбы с акцентом на один вопрос, который важен сейчас.")
                        .price(new BigDecimal("500.00")).pointsReward(10).active(true).build(),
                Service.builder().name(LoyaltyProperties.OWN_QUESTION_SERVICE_NAME)
                        .description("Свой вопрос вместо темы с карточек сайта — цена считается по количеству вопросов, без скидки за объём.")
                        .price(new BigDecimal("220.00")).pointsReward(10).active(true).build()
        ).forEach(this::ensureExists);
    }

    private void ensureExists(Service template) {
        if (serviceRepository.findByName(template.getName()).isPresent()) {
            return;
        }
        serviceRepository.save(template);
    }
}
