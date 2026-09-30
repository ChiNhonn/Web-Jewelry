package com.jewelry.store.service;

import com.jewelry.store.dto.request.AddressRequest;
import com.jewelry.store.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {

    List<AddressResponse> getUserAddresses(Long userId);

    AddressResponse getAddressById(Long userId, Long addressId);

    AddressResponse createAddress(Long userId, AddressRequest request);

    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);

    void deleteAddress(Long userId, Long addressId);

    AddressResponse setDefaultAddress(Long userId, Long addressId);
}
