package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.UpdateUserRoleRequest;
import com.jewelry.store.dto.request.UpdateUserStatusRequest;
import com.jewelry.store.dto.response.AddressResponse;
import com.jewelry.store.dto.response.AdminUserDetailResponse;
import com.jewelry.store.dto.response.AdminUserSummaryResponse;
import com.jewelry.store.entity.Address;
import com.jewelry.store.entity.Role;
import com.jewelry.store.entity.User;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.RoleType;
import com.jewelry.store.entity.enums.UserStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.OrderRepository;
import com.jewelry.store.repository.RoleRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;

    private static final List<OrderStatus> SPENDING_STATUSES = List.of(
            OrderStatus.CONFIRMED,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPING,
            OrderStatus.DELIVERED,
            OrderStatus.COMPLETED
    );

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserSummaryResponse> getAllUsers(
            String keyword, UserStatus status, RoleType role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<User> userPage = userRepository.findAllUsersWithFilter(cleanKeyword, status, role, pageable);
        Page<AdminUserSummaryResponse> responsePage = userPage.map(this::mapToSummaryResponse);
        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUserDetail(Long id) {
        User user = userRepository.findDetailByIdWithRolesAndAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDetailResponse(user);
    }

    @Override
    @Transactional
    public AdminUserDetailResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findDetailByIdWithRolesAndAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setStatus(request.getStatus());
        User saved = userRepository.save(user);
        log.info("Updated status for user id: {} to {}. Reason: '{}'", id, request.getStatus(), request.getReason());
        return mapToDetailResponse(saved);
    }

    @Override
    @Transactional
    public AdminUserDetailResponse updateUserRole(Long id, UpdateUserRoleRequest request) {
        User user = userRepository.findDetailByIdWithRolesAndAddresses(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        Set<Role> newRoles = new HashSet<>();
        for (String roleStr : request.getRoles()) {
            try {
                RoleType roleType = RoleType.valueOf(roleStr.trim().toUpperCase());
                Role role = roleRepository.findByName(roleType)
                        .orElseThrow(() -> new BadRequestException("Role không tồn tại trong hệ thống: " + roleStr));
                newRoles.add(role);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Tên vai trò không hợp lệ: " + roleStr);
            }
        }

        user.setRoles(newRoles);
        User saved = userRepository.save(user);
        log.info("Updated roles for user id: {} to {}", id, request.getRoles());
        return mapToDetailResponse(saved);
    }

    private AdminUserSummaryResponse mapToSummaryResponse(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        long totalOrders = orderRepository.countByUserId(user.getId());
        BigDecimal totalSpent = orderRepository.sumTotalSpentByUserId(user.getId(), SPENDING_STATUSES);

        return AdminUserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(roleNames)
                .totalOrders(totalOrders)
                .totalSpent(totalSpent != null ? totalSpent : BigDecimal.ZERO)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private AdminUserDetailResponse mapToDetailResponse(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        List<AddressResponse> addressResponses = user.getAddresses().stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());

        long totalOrders = orderRepository.countByUserId(user.getId());
        BigDecimal totalSpent = orderRepository.sumTotalSpentByUserId(user.getId(), SPENDING_STATUSES);

        return AdminUserDetailResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .roles(roleNames)
                .addresses(addressResponses)
                .totalOrders(totalOrders)
                .totalSpent(totalSpent != null ? totalSpent : BigDecimal.ZERO)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private AddressResponse mapToAddressResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .receiverName(a.getReceiverName())
                .phone(a.getPhone())
                .streetAddress(a.getStreetAddress())
                .ward(a.getWard())
                .district(a.getDistrict())
                .province(a.getProvince())
                .postalCode(a.getPostalCode())
                .isDefault(a.isDefault())
                .fullAddress(a.getFullAddress())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
