package com.mftplus.mftonlinestoreee.orders.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class CustomerNotFoundException extends BusinessException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
