package com.mftplus.mftonlinestoreee.users.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.users.dto.ProfileDto;
import com.mftplus.mftonlinestoreee.users.dto.ProfileRequest;
import com.mftplus.mftonlinestoreee.users.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapStructConfig.class)
public interface ProfileMapper extends BaseMapper<Profile, ProfileDto, ProfileRequest> {

    @Override
    @Mapping(source = "user.id" , target = "userId")
    ProfileDto toDto(Profile entity);

    @Override
    @Mapping(target = "user", ignore = true)
    Profile toEntity(ProfileRequest request);

    @Override
    @Mapping(target = "user", ignore = true)
    void updateEntity(ProfileRequest request, @MappingTarget Profile entity);
}
