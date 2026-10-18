package com.mftplus.mftonlinestoreee.users.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.users.dto.RoleDto;
import com.mftplus.mftonlinestoreee.users.dto.RoleRequest;
import com.mftplus.mftonlinestoreee.users.entity.Role;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface RoleMapper extends BaseMapper<Role, RoleDto, RoleRequest> {
}
