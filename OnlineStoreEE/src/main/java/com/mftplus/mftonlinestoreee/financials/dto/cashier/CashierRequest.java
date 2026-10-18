package com.mftplus.mftonlinestoreee.financials.dto.cashier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CashierRequest(
        @NotBlank @Size(max = 100) String cashierName,
        @NotBlank @Size(max = 20) String employeeCode) {
}
