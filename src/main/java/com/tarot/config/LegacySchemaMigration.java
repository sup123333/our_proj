package com.tarot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// Hibernate ddl-auto=update только добавляет колонки и никогда не удаляет старые.
// После перехода Client.email -> Client.contact старая колонка email (NOT NULL, UNIQUE)
// остаётся в БД и блокирует вставку новых клиентов. Удаляем её один раз при старте, если есть.
@Component
@RequiredArgsConstructor
@Order(0)
public class LegacySchemaMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("ALTER TABLE clients DROP COLUMN IF EXISTS email");
    }
}
