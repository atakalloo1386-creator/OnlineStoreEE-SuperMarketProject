package com.mftplus.mftonlinestoreee.financials.dto.bankaccount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record BankAccountRequest(
        @NotNull @Positive Long bankId,
        @NotBlank @Size(max = 30) String accountNumber,
        @NotBlank @Size(max = 100) String ownerName) {
}
