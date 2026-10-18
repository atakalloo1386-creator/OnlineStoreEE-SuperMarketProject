package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateBankCodeException extends BusinessException {

    public DuplicateBankCodeException(String code) {
        super("Another bank already uses the bankCode '" + code + "'");
    }
}
