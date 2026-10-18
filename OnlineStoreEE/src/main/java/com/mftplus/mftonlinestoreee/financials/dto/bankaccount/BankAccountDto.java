package com.mftplus.mftonlinestoreee.financials.dto.bankaccount;

import java.math.BigDecimal;

public record BankAccountDto(
        Long id,
        Long bankId,
        String bankName,
        String accountNumber,
        String ownerName,
        BigDecimal balance) {
}
