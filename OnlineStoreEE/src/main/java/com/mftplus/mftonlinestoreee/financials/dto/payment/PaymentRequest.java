package com.mftplus.mftonlinestoreee.financials.dto.payment;

import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull @Positive Long orderId,
        @NotNull @Positive BigDecimal amount,
        @NotNull PaymentType type) {
}
