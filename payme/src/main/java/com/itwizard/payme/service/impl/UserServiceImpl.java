package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.Account;
import com.itwizard.payme.domain.User;
import com.itwizard.payme.dto.request.UpdateUserRequest;
import com.itwizard.payme.dto.response.SearchUserResponse;
import com.itwizard.payme.dto.response.UserResponse;
import com.itwizard.payme.exception.ResourceNotFoundException;
import com.itwizard.payme.repository.AccountRepository;
import com.itwizard.payme.repository.UserRepository;
import com.itwizard.payme.repository.WalletRepository;
import com.itwizard.payme.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final WalletRepository walletRepository;

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

    @Override
    public List<SearchUserResponse> searchUsers(String query) {
        List<SearchUserResponse> results = new ArrayList<>();

        // Search by phone (exact match)
        accountRepository.findByPhone(query).ifPresent(account -> {
            walletRepository.findByUserId(account.getUser().getId()).ifPresent(wallet -> results.add(SearchUserResponse.builder()
                    .userId(account.getUser().getId())
                    .name(account.getName())
                    .phone(account.getPhone())
                    .walletId(wallet.getId())
                    .build()));
        });

        // Also search by name (partial match) - combine with phone results
        List<Account> accounts = accountRepository.findByNameContainingIgnoreCase(query);
        for (Account account : accounts) {
            // Skip if already added by phone search
            boolean alreadyAdded = results.stream()
                    .anyMatch(r -> r.getUserId().equals(account.getUser().getId()));

            if (!alreadyAdded) {
                walletRepository.findByUserId(account.getUser().getId()).ifPresent(wallet -> results.add(SearchUserResponse.builder()
                        .userId(account.getUser().getId())
                        .name(account.getName())
                        .phone(account.getPhone())
                        .walletId(wallet.getId())
                        .build()));
            }
        }

        return results;
    }

    @Override
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Account account = accountRepository.findByUserId(userId).orElse(null);

        // Update User fields
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check if email is already taken by another user
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(request.getEmail());
            userRepository.save(user);
        }

        // Update Account fields
        if (account != null) {
            boolean accountUpdated = false;
            if (request.getName() != null) {
                account.setName(request.getName());
                accountUpdated = true;
            }
            if (request.getPhone() != null) {
                // Check if phone is already taken by another account
                if (!account.getPhone().equals(request.getPhone())
                        && accountRepository.existsByPhone(request.getPhone())) {
                    throw new IllegalArgumentException("Phone number already in use");
                }
                account.setPhone(request.getPhone());
                accountUpdated = true;
            }
            if (request.getAddress() != null) {
                account.setAddress(request.getAddress());
                accountUpdated = true;
            }

            if (accountUpdated) {
                accountRepository.save(account);
            }
        }

        return UserResponse.fromEntity(user, account);
    }
}
