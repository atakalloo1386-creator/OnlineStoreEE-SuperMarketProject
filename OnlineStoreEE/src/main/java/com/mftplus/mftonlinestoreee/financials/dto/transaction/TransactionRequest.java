package com.mftplus.mftonlinestoreee.financials.dto.transaction;

import com.mftplus.mftonlinestoreee.financials.entity.enums.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public record TransactionRequest(
        @NotNull TransactionType type,
        @NotNull @Positive BigDecimal amount,
        @NotBlank @Size(max = 500) String description,
        @Positive Long cashierId,
        @Positive Long bankAccountId) {
}
