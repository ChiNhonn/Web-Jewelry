package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.UpdateUserRoleRequest;
import com.jewelry.store.dto.request.UpdateUserStatusRequest;
import com.jewelry.store.dto.response.AddressResponse;
import com.jewelry.store.dto.response.AdminUserDetailResponse;
import com.jewelry.store.dto.response.AdminUserSummaryResponse;
import com.jewelry.store.entity.Order;
import com.jewelry.store.entity.User;
import com.jewelry.store.entity.enums.RoleType;
import com.jewelry.store.entity.enums.UserStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.OrderRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserSummaryResponse> getAllUsers(String keyword, UserStatus status, RoleType role, int page, int size) {
        String needle = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<AdminUserSummaryResponse> all = userRepository.findAll().stream()
                .filter(user -> !user.isGuest())
                .filter(user -> needle.isEmpty()
                        || user.getUsername().toLowerCase(Locale.ROOT).contains(needle)
                        || user.getEmail().toLowerCase(Locale.ROOT).contains(needle)
                        || user.getFullName().toLowerCase(Locale.ROOT).contains(needle))
                .filter(user -> status == null || user.getStatus() == status)
                .filter(user -> role == null || roleName(user).equals(role.name()))
                .map(this::toSummary)
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;
        int from = Math.min(safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        return PageResponse.<AdminUserSummaryResponse>builder()
                .content(all.subList(from, to))
                .page(safePage)
                .size(safeSize)
                .totalElements(all.size())
                .totalPages(Math.max((int) Math.ceil(all.size() / (double) safeSize), 1))
                .first(safePage == 0)
                .last(to >= all.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUserDetail(Long id) {
        return toDetail(load(id));
    }

    @Override
    @Transactional
    public AdminUserDetailResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = load(id);
        if (user.isGuest()) {
            throw new BadRequestException("Không khóa tài khoản khách vãng lai");
        }
        user.setLocked(request.getStatus() == UserStatus.LOCKED || request.getStatus() == UserStatus.INACTIVE);
        return toDetail(userRepository.save(user));
    }

    @Override
    @Transactional
    public AdminUserDetailResponse updateUserRole(Long id, UpdateUserRoleRequest request) {
        User user = load(id);
        String role = request.getRoles().stream().findFirst().orElse("ROLE_CUSTOMER");
        if (role.endsWith("ADMIN")) {
            user.setRole("ADMIN");
        } else {
            user.setRole("CUSTOMER");
        }
        return toDetail(userRepository.save(user));
    }

    private User load(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
    }

    private String roleName(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole()) ? "ROLE_ADMIN" : "ROLE_CUSTOMER";
    }

    private AdminUserSummaryResponse toSummary(User user) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        BigDecimal spent = orders.stream()
                .filter(order -> "Đã giao hàng".equals(order.getOrderStatusText()))
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return AdminUserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .firstName(user.getFullName())
                .lastName("")
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(List.of(roleName(user)))
                .totalOrders(orders.size())
                .totalSpent(spent)
                .build();
    }

    private AdminUserDetailResponse toDetail(User user) {
        AdminUserSummaryResponse summary = toSummary(user);
        return AdminUserDetailResponse.builder()
                .id(summary.getId())
                .username(summary.getUsername())
                .email(summary.getEmail())
                .fullName(summary.getFullName())
                .firstName(summary.getFirstName())
                .phone(summary.getPhone())
                .status(summary.getStatus())
                .roles(summary.getRoles())
                .totalOrders(summary.getTotalOrders())
                .totalSpent(summary.getTotalSpent())
                .addresses(user.getAddress() == null ? List.of() : List.of(AddressResponse.builder()
                        .id(user.getId())
                        .receiverName(user.getFullName())
                        .phone(user.getPhone())
                        .streetAddress(user.getAddress())
                        .fullAddress(user.getAddress())
                        .isDefault(true)
                        .build()))
                .build();
    }
}
