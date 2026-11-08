package com.jewelry.store.service;

import com.jewelry.store.entity.Role;
import com.jewelry.store.entity.User;
import com.jewelry.store.entity.enums.RoleType;
import com.jewelry.store.entity.enums.UserStatus;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.RoleRepository;
import com.jewelry.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestUserProvider {

    public static final String GUEST_USERNAME = "khach-vang-lai";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User getOrCreate() {
        return userRepository.findByUsernameOrEmailWithRoles(GUEST_USERNAME)
                .orElseGet(this::createGuest);
    }

    private User createGuest() {
        Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò khách hàng"));
        User guest = User.builder()
                .username(GUEST_USERNAME)
                .email("khach.vanglai@aurajewelry.local")
                .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                .firstName("Khách")
                .lastName("Vãng lai")
                .status(UserStatus.INACTIVE)
                .roles(Set.of(customerRole))
                .build();
        return userRepository.save(guest);
    }
}
