package com.itwizard.payme.controller;

import com.itwizard.payme.dto.request.LoginRequest;
import com.itwizard.payme.dto.request.RegisterRequest;
import com.itwizard.payme.dto.response.AuthResponse;
import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<StandardResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(StandardResponse.success(response, "Login successful"));
    }
}
