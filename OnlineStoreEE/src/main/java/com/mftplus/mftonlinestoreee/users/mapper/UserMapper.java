package com.mftplus.mftonlinestoreee.users.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.users.dto.UserDto;
import com.mftplus.mftonlinestoreee.users.dto.UserRequest;
import com.mftplus.mftonlinestoreee.users.entity.Role;
import com.mftplus.mftonlinestoreee.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface UserMapper extends BaseMapper<User, UserDto, UserRequest> {
    default String mapRoleToName(Role role){
        return role.getName();
    }


    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserRequest request);


    @Override
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntity(UserRequest request, @MappingTarget User entity);
}
