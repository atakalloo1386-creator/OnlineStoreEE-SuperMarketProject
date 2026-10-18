package com.mftplus.mftonlinestoreee.financials.dto.payment;

import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentStatus;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record PaymentDto(
        Long id,
        Long orderId,
        PaymentType type,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime createdAt) {
}
