package com.mftplus.mftonlinestoreee.orders.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class InactiveProductException extends BusinessException {
    public InactiveProductException(String message) {
        super(message);
    }
}