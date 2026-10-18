package com.mftplus.mftonlinestoreee.users.dto;

import java.time.LocalDate;

public record ProfileDto(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,
        String address

) {}
