package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.User;
import com.itwizard.payme.dto.response.UserResponse;
import com.itwizard.payme.exception.ResourceNotFoundException;
import com.itwizard.payme.repository.UserRepository;
import com.itwizard.payme.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final com.itwizard.payme.repository.AccountRepository accountRepository;

    @Override
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        com.itwizard.payme.domain.Account account = accountRepository.findByUserId(userId).orElse(null);

        return UserResponse.fromEntity(user, account);
    }

    @Override
    public UserResponse getCurrentUser(UUID userId) {
        return getUserById(userId);
    }
}
