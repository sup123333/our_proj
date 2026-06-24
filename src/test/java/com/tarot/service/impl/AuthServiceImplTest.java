package com.tarot.service.impl;

import com.tarot.dto.auth.LoginRequest;
import com.tarot.dto.auth.RegisterRequest;
import com.tarot.entity.Client;
import com.tarot.exception.BadRequestException;
import com.tarot.exception.DuplicateContactException;
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
    void register_hashesPasswordAndNormalizesPhone() {
        authService = service();
        when(clientRepository.findByContact("+79991234567")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass1")).thenReturn("hashed");
        when(jwtService.generateToken("+79991234567")).thenReturn("token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        var response = authService.register(
                new RegisterRequest("Anna", "pass1", "+7 (999) 123-45-67", null));

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertThat(captor.getValue().getContact()).isEqualTo("+79991234567");
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed");
        assertThat(response.token()).isEqualTo("token");
    }

    @Test
    void register_normalizesTelegramHandle() {
        authService = service();
        when(clientRepository.findByContact("vika")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass1")).thenReturn("hashed");
        when(jwtService.generateToken("vika")).thenReturn("token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        authService.register(new RegisterRequest("Anna", "pass1", null, "@Vika"));

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertThat(captor.getValue().getContact()).isEqualTo("vika");
    }

    @Test
    void register_rejectsMissingContact() {
        authService = service();

        assertThatThrownBy(() -> authService.register(new RegisterRequest("Anna", "pass1", null, null)))
                .isInstanceOf(BadRequestException.class);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void register_rejectsDuplicateContact() {
        authService = service();
        when(clientRepository.findByContact("+79991234567")).thenReturn(Optional.of(new Client()));

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("Anna", "pass1", "+79991234567", null)))
                .isInstanceOf(DuplicateContactException.class);

        verify(clientRepository, never()).save(any());
    }

    @Test
    void login_rejectsWrongPassword() {
        authService = service();
        Client client = Client.builder().contact("+79991234567").password("hashed").build();
        when(clientRepository.findByContact("+79991234567")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("+79991234567", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_rejectsUnknownContact() {
        authService = service();
        when(clientRepository.findByContact("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("ghost", "anything")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_succeedsWithCorrectPassword() {
        authService = service();
        Client client = Client.builder().contact("+79991234567").password("hashed").build();
        when(clientRepository.findByContact("+79991234567")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("correct", "hashed")).thenReturn(true);
        when(jwtService.generateToken("+79991234567")).thenReturn("token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        var response = authService.login(new LoginRequest("+79991234567", "correct"));

        assertThat(response.token()).isEqualTo("token");
    }
}
