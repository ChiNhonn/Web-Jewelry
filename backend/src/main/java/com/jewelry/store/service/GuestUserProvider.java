package com.jewelry.store.service;

import com.jewelry.store.entity.User;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestUserProvider {

    public static final String GUEST_USERNAME = "__guest__";

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getOrCreate() {
        return userRepository.findByUsername(GUEST_USERNAME)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản khách vãng lai"));
    }
}
