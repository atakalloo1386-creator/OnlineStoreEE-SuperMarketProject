package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class CashierNotFoundException extends BusinessException {

    public CashierNotFoundException(Long cashierId) {
        super("Cashier " + cashierId + " not found");
    }
}
