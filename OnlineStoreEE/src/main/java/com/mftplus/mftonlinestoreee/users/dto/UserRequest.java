package com.mftplus.mftonlinestoreee.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserRequest(
        @NotBlank @Size(max = 60) String username,
        @NotBlank @Size(min = 8, max = 72) String password,   // BCrypt reads at most 72 bytes
        Boolean enabled,                                     // null keeps the current value (true for a new user)
        Set<Long> roleIds
) {}
