package com.jewelry.store.mapper;

import com.jewelry.store.dto.request.AddressRequest;
import com.jewelry.store.dto.response.AddressResponse;
import com.jewelry.store.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDefault", source = "isDefault")
    Address toEntity(AddressRequest request);

    @Mapping(target = "fullAddress", expression = "java(address.getFullAddress())")
    @Mapping(target = "isDefault", expression = "java(address.isDefault())")
    AddressResponse toResponse(Address address);

    List<AddressResponse> toResponseList(List<Address> addresses);
}
