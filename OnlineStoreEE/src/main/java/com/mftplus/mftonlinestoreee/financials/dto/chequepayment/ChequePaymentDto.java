package com.mftplus.mftonlinestoreee.financials.dto.chequepayment;

import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentStatus;
import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ChequePaymentDto(
        Long id,
        Long orderId,
        PaymentType type,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime createdAt,
        Long bankId,
        String chequeNumber,
        LocalDate dueDate) {
}
