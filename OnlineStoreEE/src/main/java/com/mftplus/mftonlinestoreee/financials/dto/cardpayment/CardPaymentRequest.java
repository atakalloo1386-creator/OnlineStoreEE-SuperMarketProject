package com.mftplus.mftonlinestoreee.financials.dto.cardpayment;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CardPaymentRequest(
        @NotNull @Positive Long orderId,
        @NotNull @Positive  BigDecimal amount,
        @NotBlank @Pattern(regexp = "\\d{4}", message = "must be exactly 4 digits") String cardLastDigits,
        @NotBlank @Size(max = 50) String trackingCode) {
}
