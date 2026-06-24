package com.tarot.service.impl;

import com.tarot.dto.auth.AuthResponse;
import com.tarot.dto.auth.LoginRequest;
import com.tarot.dto.auth.RegisterRequest;
import com.tarot.entity.Client;
import com.tarot.exception.BadRequestException;
import com.tarot.exception.DuplicateContactException;
import com.tarot.exception.InvalidCredentialsException;
import com.tarot.repository.ClientRepository;
import com.tarot.security.JwtService;
import com.tarot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String phone = blankToNull(request.phone());
        String telegram = blankToNull(request.telegram());
        if (phone == null && telegram == null) {
            throw new BadRequestException("Укажи телефон или telegram, чтобы с тобой можно было связаться");
        }
        String contact = normalizeContact(phone != null ? phone : telegram);
        if (clientRepository.findByContact(contact).isPresent()) {
            throw new DuplicateContactException(contact);
        }

        Client client = Client.builder()
                .name(request.name())
                .contact(contact)
                .password(passwordEncoder.encode(request.password()))
                .phone(phone)
                .telegram(telegram)
                .build();
        clientRepository.save(client);

        return issueToken(contact);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String contact = normalizeContact(request.contact());
        Client client = clientRepository.findByContact(contact)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), client.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return issueToken(contact);
    }

    private AuthResponse issueToken(String contact) {
        String token = jwtService.generateToken(contact);
        return new AuthResponse(token, jwtService.getExpirationMs());
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    // Телефон сводим к цифрам (+код), telegram — к нижнему регистру без @, чтобы один и тот же
    // контакт не давал разные написания при регистрации и входе.
    private String normalizeContact(String raw) {
        String trimmed = raw.trim();
        if (trimmed.matches("^[+\\d][\\d\\s()-]{3,}$")) {
            return trimmed.replaceAll("[^+\\d]", "");
        }
        return trimmed.toLowerCase().replaceFirst("^@", "");
    }
}
