package com.jewelry.store.service;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.UpdateUserRoleRequest;
import com.jewelry.store.dto.request.UpdateUserStatusRequest;
import com.jewelry.store.dto.response.AdminUserDetailResponse;
import com.jewelry.store.dto.response.AdminUserSummaryResponse;
import com.jewelry.store.entity.enums.RoleType;
import com.jewelry.store.entity.enums.UserStatus;

public interface AdminUserService {

    PageResponse<AdminUserSummaryResponse> getAllUsers(String keyword, UserStatus status, RoleType role, int page, int size);

    AdminUserDetailResponse getUserDetail(Long id);

    AdminUserDetailResponse updateUserStatus(Long id, UpdateUserStatusRequest request);

    AdminUserDetailResponse updateUserRole(Long id, UpdateUserRoleRequest request);
}
