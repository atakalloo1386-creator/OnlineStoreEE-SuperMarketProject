package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class BankAccountNotFoundException extends BusinessException {

    public BankAccountNotFoundException(Long bankAccountId) {
        super("Bank account " + bankAccountId + " not found");
    }
}
