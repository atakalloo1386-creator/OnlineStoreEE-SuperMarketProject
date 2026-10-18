package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class BankNotFoundException extends BusinessException {

    public BankNotFoundException(Long bankId) {
        super("Bank " + bankId + " not found");
    }
}
