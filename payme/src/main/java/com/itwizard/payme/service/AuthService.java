package com.itwizard.payme.service;

import com.itwizard.payme.dto.request.LoginRequest;
import com.itwizard.payme.dto.request.RegisterRequest;
import com.itwizard.payme.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
