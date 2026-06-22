package com.tarot.service.impl;

import com.tarot.dto.auth.AuthResponse;
import com.tarot.dto.auth.LoginRequest;
import com.tarot.dto.auth.RegisterRequest;
import com.tarot.entity.Client;
import com.tarot.exception.DuplicateEmailException;
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
        String email = normalize(request.email());
        if (clientRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException(email);
        }

        Client client = Client.builder()
                .name(request.name())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .telegram(request.telegram())
                .build();
        clientRepository.save(client);

        return issueToken(email);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = normalize(request.email());
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), client.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return issueToken(email);
    }

    private AuthResponse issueToken(String email) {
        String token = jwtService.generateToken(email);
        return new AuthResponse(token, jwtService.getExpirationMs());
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
