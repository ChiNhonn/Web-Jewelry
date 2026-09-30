package com.jewelry.store.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRoleRequest {

    @NotEmpty(message = "Danh sách vai trò không được để trống")
    private Set<String> roles; // VD: ["ROLE_CUSTOMER", "ROLE_STAFF", "ROLE_ADMIN"]
}
