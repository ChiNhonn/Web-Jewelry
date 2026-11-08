package com.jewelry.store.mapper;

import com.jewelry.store.dto.request.AddressRequest;
import com.jewelry.store.dto.response.AddressResponse;
import com.jewelry.store.entity.Address;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-08T19:42:08+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public Address toEntity(AddressRequest request) {
        if ( request == null ) {
            return null;
        }

        Address.AddressBuilder address = Address.builder();

        if ( request.getIsDefault() != null ) {
            address.isDefault( request.getIsDefault() );
        }
        address.district( request.getDistrict() );
        address.phone( request.getPhone() );
        address.postalCode( request.getPostalCode() );
        address.province( request.getProvince() );
        address.receiverName( request.getReceiverName() );
        address.streetAddress( request.getStreetAddress() );
        address.ward( request.getWard() );

        return address.build();
    }

    @Override
    public AddressResponse toResponse(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressResponse.AddressResponseBuilder addressResponse = AddressResponse.builder();

        addressResponse.createdAt( address.getCreatedAt() );
        addressResponse.district( address.getDistrict() );
        addressResponse.id( address.getId() );
        addressResponse.phone( address.getPhone() );
        addressResponse.postalCode( address.getPostalCode() );
        addressResponse.province( address.getProvince() );
        addressResponse.receiverName( address.getReceiverName() );
        addressResponse.streetAddress( address.getStreetAddress() );
        addressResponse.ward( address.getWard() );

        addressResponse.fullAddress( address.getFullAddress() );
        addressResponse.isDefault( address.isDefault() );

        return addressResponse.build();
    }

    @Override
    public List<AddressResponse> toResponseList(List<Address> addresses) {
        if ( addresses == null ) {
            return null;
        }

        List<AddressResponse> list = new ArrayList<AddressResponse>( addresses.size() );
        for ( Address address : addresses ) {
            list.add( toResponse( address ) );
        }

        return list;
    }
}
