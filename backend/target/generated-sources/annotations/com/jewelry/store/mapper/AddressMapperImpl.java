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
    date = "2026-11-09T00:45:38+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Oracle Corporation)"
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
        address.receiverName( request.getReceiverName() );
        address.phone( request.getPhone() );
        address.streetAddress( request.getStreetAddress() );
        address.ward( request.getWard() );
        address.district( request.getDistrict() );
        address.province( request.getProvince() );
        address.postalCode( request.getPostalCode() );

        return address.build();
    }

    @Override
    public AddressResponse toResponse(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressResponse.AddressResponseBuilder addressResponse = AddressResponse.builder();

        addressResponse.id( address.getId() );
        addressResponse.receiverName( address.getReceiverName() );
        addressResponse.phone( address.getPhone() );
        addressResponse.streetAddress( address.getStreetAddress() );
        addressResponse.ward( address.getWard() );
        addressResponse.district( address.getDistrict() );
        addressResponse.province( address.getProvince() );
        addressResponse.postalCode( address.getPostalCode() );
        addressResponse.createdAt( address.getCreatedAt() );

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
