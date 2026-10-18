package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class BankInUseException extends BusinessException {

    public BankInUseException(Long bankId) {
        super("Bank " + bankId + " still has bank accounts and cannot be deleted");
    }
}
