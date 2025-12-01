package com.itwizard.payme.dto.response;

import com.itwizard.payme.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user, com.itwizard.payme.domain.Account account) {
        return UserResponse.builder()
                .id(user.getId())
                .name(account != null ? account.getName() : null)
                .email(user.getEmail())
                .phone(account != null ? account.getPhone() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
