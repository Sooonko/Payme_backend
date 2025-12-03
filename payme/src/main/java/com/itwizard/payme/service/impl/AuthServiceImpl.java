package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.Account;
import com.itwizard.payme.domain.User;
import com.itwizard.payme.dto.request.LoginRequest;
import com.itwizard.payme.dto.request.RegisterRequest;
import com.itwizard.payme.dto.response.AuthResponse;
import com.itwizard.payme.dto.response.UserResponse;
import com.itwizard.payme.exception.InvalidCredentialsException;
import com.itwizard.payme.exception.UserAlreadyExistsException;
import com.itwizard.payme.repository.AccountRepository;
import com.itwizard.payme.repository.UserRepository;

import com.itwizard.payme.security.JwtTokenProvider;
import com.itwizard.payme.service.AuditService;
import com.itwizard.payme.service.AuthService;
import com.itwizard.payme.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditService auditService;
    private final WalletService walletService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        // Check if phone exists (now in Account)
        if (accountRepository.existsByPhone(request.getPhone())) {
            throw new UserAlreadyExistsException("Phone number already registered");
        }

        // Create User (Auth)
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        user = userRepository.save(user);

        // Create Account (Profile)
        Account account = Account.builder()
                .user(user)
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        accountRepository.save(account);

        // Create Wallet with 0 balance
        walletService.createWallet(user.getId(), "MNT");

        // Log registration
        auditService.logAction(user.getId(), "REGISTER", "User registered: " + user.getEmail(), null);

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromEntity(user, account))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Log failed login attempt (optional, but good for security)
            auditService.logAction(user.getId(), "LOGIN_FAILED", "Failed login attempt for: " + request.getEmail(),
                    null);
            throw new InvalidCredentialsException("Invalid email or password");
        }
        // Generate token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        // Log successful login
        auditService.logAction(user.getId(), "LOGIN", "User logged in", null);

        com.itwizard.payme.domain.Account account = accountRepository.findByUserId(user.getId()).orElse(null);

        return AuthResponse.builder()
                .token(token)
                .user(UserResponse.fromEntity(user, account))
                .build();
    }
}
