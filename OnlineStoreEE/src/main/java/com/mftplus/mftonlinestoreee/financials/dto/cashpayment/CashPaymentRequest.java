package com.mftplus.mftonlinestoreee.financials.dto.cashpayment;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CashPaymentRequest(
        @NotNull @Positive Long orderId,
        @NotNull @Positive  BigDecimal amount,
        @NotNull @Positive Long cashierId) {
}
