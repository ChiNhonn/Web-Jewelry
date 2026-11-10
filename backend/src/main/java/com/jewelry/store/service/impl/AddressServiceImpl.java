package com.jewelry.store.service.impl;

import com.jewelry.store.dto.request.AddressRequest;
import com.jewelry.store.dto.response.AddressResponse;
import com.jewelry.store.entity.User;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(Long userId) {
        User user = load(userId);
        if (user.getAddress() == null || user.getAddress().isBlank()) {
            return List.of();
        }
        return List.of(toResponse(user));
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long userId, Long addressId) {
        return toResponse(load(userId));
    }

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        return save(userId, request);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        return save(userId, request);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        User user = load(userId);
        user.setAddress(null);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        return toResponse(load(userId));
    }

    private AddressResponse save(Long userId, AddressRequest request) {
        User user = load(userId);
        user.setFullName(request.getReceiverName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getStreetAddress() + ", " + request.getWard() + ", " + request.getDistrict() + ", " + request.getProvince());
        return toResponse(userRepository.save(user));
    }

    private User load(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
    }

    private AddressResponse toResponse(User user) {
        return AddressResponse.builder()
                .id(user.getId())
                .receiverName(user.getFullName())
                .phone(user.getPhone())
                .streetAddress(user.getAddress())
                .fullAddress(user.getAddress())
                .isDefault(true)
                .build();
    }
}
