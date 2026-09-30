package com.jewelry.store.controller.admin;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.UpdateUserRoleRequest;
import com.jewelry.store.dto.request.UpdateUserStatusRequest;
import com.jewelry.store.dto.response.AdminUserDetailResponse;
import com.jewelry.store.dto.response.AdminUserSummaryResponse;
import com.jewelry.store.entity.enums.RoleType;
import com.jewelry.store.entity.enums.UserStatus;
import com.jewelry.store.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin User & Customer Management", description = "Endpoints for moderating customer accounts, managing roles, and reviewing purchase histories")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "Get paginated user list", description = "Lấy danh sách người dùng kèm phân trang, lọc theo vai trò (ROLE_CUSTOMER, ROLE_STAFF, ROLE_ADMIN), trạng thái và tìm kiếm")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserSummaryResponse>>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) RoleType role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AdminUserSummaryResponse> response = adminUserService.getAllUsers(keyword, status, role, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile detail by ID", description = "Xem chi tiết hồ sơ người dùng, sổ địa chỉ và lịch sử tổng chi tiêu tích lũy")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> getUserDetail(@PathVariable Long id) {
        AdminUserDetailResponse response = adminUserService.getUserDetail(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update user account status (Lock/Unlock)", description = "Khóa tài khoản vi phạm hoặc kích hoạt lại tài khoản khách hàng")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        AdminUserDetailResponse response = adminUserService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái tài khoản thành công!", response));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Assign/Update user roles", description = "Phân quyền quản trị hoặc nhân viên vận hành cho tài khoản")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        AdminUserDetailResponse response = adminUserService.updateUserRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật vai trò người dùng thành công!", response));
    }
}
