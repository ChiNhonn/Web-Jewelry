package com.jewelry.store.mapper;

import com.jewelry.store.dto.response.UserSummaryResponse;
import com.jewelry.store.entity.Role;
import com.jewelry.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStringList")
    UserSummaryResponse toUserSummaryResponse(User user);

    @Named("mapRolesToStringList")
    default List<String> mapRolesToStringList(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(role -> role.getName().name())
                .toList();
    }
}
