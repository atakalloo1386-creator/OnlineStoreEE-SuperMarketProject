package com.mftplus.mftonlinestoreee.users.dto;


import java.util.Set;

public record UserDto(
        Long id,
        String username,
        boolean enabled,
        Set<String> roles
) {}
