package com.tarot.security;

import com.tarot.repository.ClientRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final ClientRepository clientRepository;

    // Email таролога — задаётся в application.properties
    @Value("${admin.email}")
    private String adminEmail;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            authenticate(token);
        } catch (Exception e) {
            // Невалидный/просроченный токен не должен валить запрос 500-кой — просто остаёмся неаутентифицированными,
            // дальше решает authorizeHttpRequests/AccessDeniedHandler.
            log.debug("Не удалось аутентифицировать токен: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }

    private void authenticate(String token) {
        if (!jwtService.isValid(token)) {
            return;
        }

        String email = jwtService.extractEmail(token);
        var clientOpt = clientRepository.findByEmail(email);
        if (clientOpt.isEmpty()) {
            return;
        }

        List<SimpleGrantedAuthority> authorities = email.equalsIgnoreCase(adminEmail)
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));

        var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
