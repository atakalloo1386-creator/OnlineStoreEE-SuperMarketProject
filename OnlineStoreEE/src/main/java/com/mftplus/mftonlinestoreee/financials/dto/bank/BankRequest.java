package com.mftplus.mftonlinestoreee.financials.dto.bank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BankRequest(
        @NotBlank @Size(max = 100) String bankName,
        @NotBlank @Size(max = 20) String bankCode) {
}
