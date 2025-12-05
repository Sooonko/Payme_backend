package com.itwizard.payme.controller;

import com.itwizard.payme.dto.request.UpdateUserRequest;
import com.itwizard.payme.dto.response.SearchUserResponse;
import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.dto.response.UserResponse;
import com.itwizard.payme.security.CurrentUser;
import com.itwizard.payme.security.UserPrincipal;
import com.itwizard.payme.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserResponse>> getCurrentUser(
            @CurrentUser @NotNull UserPrincipal userPrincipal) {
        UserResponse response = userService.getCurrentUser(userPrincipal.getId());
        return ResponseEntity.ok(StandardResponse.success(response, "User retrieved successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<StandardResponse<UserResponse>> updateUser(
            @CurrentUser @NotNull UserPrincipal userPrincipal,
            @RequestBody @Valid UpdateUserRequest request) {
        UserResponse response = userService.updateUser(userPrincipal.getId(), request);
        return ResponseEntity.ok(StandardResponse.success(response, "User updated successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(StandardResponse.success(response, "User retrieved successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<StandardResponse<List<SearchUserResponse>>> searchUsers(
            @RequestParam String query) {
        List<SearchUserResponse> results = userService.searchUsers(query);
        return ResponseEntity.ok(StandardResponse.success(results, "Search completed successfully"));
    }
}
