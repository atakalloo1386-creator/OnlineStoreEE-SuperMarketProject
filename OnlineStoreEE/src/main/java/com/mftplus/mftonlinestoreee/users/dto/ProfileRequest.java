package com.mftplus.mftonlinestoreee.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProfileRequest(
        @NotBlank @Size(max = 30) String firstName,
        @NotBlank @Size(max = 30) String lastName,
        @Size(max = 12) String phoneNumber,
        @Past LocalDate birthDate,
        @Size(max = 100) String address

){}
