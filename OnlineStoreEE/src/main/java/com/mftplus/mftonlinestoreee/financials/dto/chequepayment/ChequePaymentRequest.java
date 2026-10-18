package com.mftplus.mftonlinestoreee.financials.dto.chequepayment;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChequePaymentRequest(
        @NotNull @Positive Long orderId,
        @NotNull @Positive  BigDecimal amount,
        @NotNull @Positive Long bankId,
        @NotBlank @Size(max = 20) String chequeNumber,
        @NotNull LocalDate dueDate) {
}
