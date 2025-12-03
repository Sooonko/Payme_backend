package com.itwizard.payme.service;

import com.itwizard.payme.dto.response.SearchUserResponse;
import com.itwizard.payme.dto.response.UserResponse;

import java.util.List;

import java.util.UUID;

public interface UserService {
    UserResponse getUserById(UUID userId);

    UserResponse getCurrentUser(UUID userId);

    List<SearchUserResponse> searchUsers(String query);
}
