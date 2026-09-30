package com.jewelry.store.service.impl;

import com.jewelry.store.dto.request.AddressRequest;
import com.jewelry.store.dto.response.AddressResponse;
import com.jewelry.store.entity.Address;
import com.jewelry.store.entity.User;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.mapper.AddressMapper;
import com.jewelry.store.repository.AddressRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
        return addressMapper.toResponseList(addresses);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tồn tại hoặc không thuộc quyền sở hữu của bạn"));
        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        boolean hasExistingAddresses = addressRepository.existsByUserId(userId);
        boolean isDefault = !hasExistingAddresses || Boolean.TRUE.equals(request.getIsDefault());

        if (isDefault && hasExistingAddresses) {
            clearCurrentDefaultAddress(userId);
        }

        Address address = addressMapper.toEntity(request);
        address.setUser(user);
        address.setDefault(isDefault);

        Address saved = addressRepository.save(address);
        log.info("Created new shipping address ID: {} for user: {}", saved.getId(), userId);
        return addressMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tồn tại"));

        boolean requestedDefault = Boolean.TRUE.equals(request.getIsDefault());
        if (requestedDefault && !address.isDefault()) {
            clearCurrentDefaultAddress(userId);
            address.setDefault(true);
        }

        address.setReceiverName(request.getReceiverName());
        address.setPhone(request.getPhone());
        address.setStreetAddress(request.getStreetAddress());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setProvince(request.getProvince());
        address.setPostalCode(request.getPostalCode());

        Address updated = addressRepository.save(address);
        log.info("Updated shipping address ID: {} for user: {}", addressId, userId);
        return addressMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tồn tại"));

        if (address.isDefault()) {
            throw new BadRequestException("Không thể xóa địa chỉ mặc định. Vui lòng thiết lập địa chỉ khác làm mặc định trước");
        }

        addressRepository.delete(address);
        log.info("Deleted shipping address ID: {} for user: {}", addressId, userId);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Địa chỉ không tồn tại"));

        if (!address.isDefault()) {
            clearCurrentDefaultAddress(userId);
            address.setDefault(true);
            addressRepository.save(address);
        }

        return addressMapper.toResponse(address);
    }

    private void clearCurrentDefaultAddress(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(existingDefault -> {
                    existingDefault.setDefault(false);
                    addressRepository.save(existingDefault);
                });
    }
}
