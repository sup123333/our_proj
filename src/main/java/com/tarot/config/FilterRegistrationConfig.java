package com.tarot.config;

import com.tarot.security.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JwtAuthenticationFilter и RateLimitFilter — это @Component-бины, поэтому Spring Boot по умолчанию
 * регистрирует их и как обычные servlet-фильтры контейнера, и они же добавляются вручную в
 * SecurityFilterChain через addFilterBefore. Без этого класса каждый фильтр выполнялся бы дважды
 * на запрос (для rate-limit это незаметно урезает реальный лимит запросов вдвое).
 */
@Configuration
public class FilterRegistrationConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
            JwtAuthenticationFilter filter) {
        return disabled(filter);
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(RateLimitFilter filter) {
        return disabled(filter);
    }

    private <T extends Filter> FilterRegistrationBean<T> disabled(T filter) {
        FilterRegistrationBean<T> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
