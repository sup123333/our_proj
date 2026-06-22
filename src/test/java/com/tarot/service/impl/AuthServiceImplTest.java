package com.tarot.service.impl;

import com.tarot.dto.auth.LoginRequest;
import com.tarot.dto.auth.RegisterRequest;
import com.tarot.entity.Client;
import com.tarot.exception.DuplicateEmailException;
import com.tarot.exception.InvalidCredentialsException;
import com.tarot.repository.ClientRepository;
import com.tarot.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    private AuthServiceImpl authService;

    private AuthServiceImpl service() {
        return new AuthServiceImpl(clientRepository, passwordEncoder, jwtService);
    }

    @Test
    void register_hashesPasswordAndNormalizesEmail() {
        authService = service();
        when(clientRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword123")).thenReturn("hashed");
        when(jwtService.generateToken("user@test.com")).thenReturn("token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        var response = authService.register(
                new RegisterRequest("Anna", "USER@Test.com", "rawPassword123", null, null));

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("user@test.com");
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed");
        assertThat(response.token()).isEqualTo("token");
    }

    @Test
    void register_rejectsDuplicateEmail() {
        authService = service();
        when(clientRepository.findByEmail("user@test.com")).thenReturn(Optional.of(new Client()));

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("Anna", "user@test.com", "rawPassword123", null, null)))
                .isInstanceOf(DuplicateEmailException.class);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void login_rejectsWrongPassword() {
        authService = service();
        Client client = Client.builder().email("user@test.com").password("hashed").build();
        when(clientRepository.findByEmail("user@test.com")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@test.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_rejectsUnknownEmail() {
        authService = service();
        when(clientRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("ghost@test.com", "anything")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_succeedsWithCorrectPassword() {
        authService = service();
        Client client = Client.builder().email("user@test.com").password("hashed").build();
        when(clientRepository.findByEmail("user@test.com")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("correct", "hashed")).thenReturn(true);
        when(jwtService.generateToken("user@test.com")).thenReturn("token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        var response = authService.login(new LoginRequest("user@test.com", "correct"));

        assertThat(response.token()).isEqualTo("token");
    }
}
