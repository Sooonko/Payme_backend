package com.itwizard.payme.controller;

import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.dto.response.UserResponse;
import com.itwizard.payme.security.JwtTokenProvider;
import com.itwizard.payme.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserResponse>> getCurrentUser(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer " prefix
        UUID userId = jwtTokenProvider.getUserIdFromToken(jwt);
        UserResponse response = userService.getCurrentUser(userId);
        return ResponseEntity.ok(StandardResponse.success(response, "User retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(StandardResponse.success(response, "User retrieved successfully"));
    }
}
