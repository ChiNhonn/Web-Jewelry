package com.jewelry.store.mapper;

import com.jewelry.store.dto.response.UserSummaryResponse;
import com.jewelry.store.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-08T19:42:09+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserSummaryResponse toUserSummaryResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserSummaryResponse.UserSummaryResponseBuilder userSummaryResponse = UserSummaryResponse.builder();

        userSummaryResponse.roles( mapRolesToStringList( user.getRoles() ) );
        userSummaryResponse.avatarUrl( user.getAvatarUrl() );
        userSummaryResponse.email( user.getEmail() );
        userSummaryResponse.firstName( user.getFirstName() );
        userSummaryResponse.id( user.getId() );
        userSummaryResponse.lastName( user.getLastName() );
        userSummaryResponse.phone( user.getPhone() );
        userSummaryResponse.username( user.getUsername() );

        userSummaryResponse.fullName( user.getFullName() );

        return userSummaryResponse.build();
    }
}
