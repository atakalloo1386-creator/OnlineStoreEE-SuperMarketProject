package com.mftplus.mftonlinestoreee.users.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.users.dto.ProfileDto;
import com.mftplus.mftonlinestoreee.users.dto.ProfileRequest;
import com.mftplus.mftonlinestoreee.users.dto.UserRequest;
import com.mftplus.mftonlinestoreee.users.dto.UserDto;

import java.util.Optional;

public interface UserService extends BaseService<UserRequest, UserDto> {

    Optional<ProfileDto> findProfile(Long userId);

    Optional<ProfileDto> saveProfile(Long userId, ProfileRequest request);




}
