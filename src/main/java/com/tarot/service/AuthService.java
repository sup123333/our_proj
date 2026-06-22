package com.tarot.service;

import com.tarot.dto.auth.AuthResponse;
import com.tarot.dto.auth.LoginRequest;
import com.tarot.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
