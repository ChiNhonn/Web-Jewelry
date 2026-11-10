package com.jewelry.store.mapper;

import com.jewelry.store.dto.response.UserSummaryResponse;
import com.jewelry.store.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserSummaryResponse toUserSummaryResponse(User user) {
        String role = user.getRole() == null ? "CUSTOMER" : user.getRole().toUpperCase();
        if (!role.startsWith("ROLE_") && !"GUEST".equals(role)) {
            role = "ROLE_" + role;
        }
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            role = "ROLE_ADMIN";
        } else if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            role = "ROLE_CUSTOMER";
        }
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .firstName(user.getFullName())
                .lastName("")
                .phone(user.getPhone())
                .roles(List.of(role))
                .build();
    }
}
