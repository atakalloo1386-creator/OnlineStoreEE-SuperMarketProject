package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

import java.math.BigDecimal;

public class InsufficientBalanceException extends BusinessException {

    public InsufficientBalanceException(BigDecimal balance, BigDecimal amount) {
        super("Balance " + balance + " is not enough to withdraw " + amount);
    }
}
