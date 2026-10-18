package com.mftplus.mftonlinestoreee.financials.dto.transaction;

import com.mftplus.mftonlinestoreee.financials.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        Long id,
        Long bankAccountId,
        TransactionType type,
        BigDecimal amount,
        String description,
        Long cashierId,
        LocalDateTime createdAt) {
}
