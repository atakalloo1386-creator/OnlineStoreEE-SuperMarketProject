package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateAccountNumberException extends BusinessException {

    public DuplicateAccountNumberException(String accountNumber) {
        super("Another bank account already has the number '" + accountNumber + "'");
    }
}
