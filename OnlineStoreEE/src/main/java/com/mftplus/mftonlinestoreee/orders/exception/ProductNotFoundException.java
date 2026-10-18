package com.mftplus.mftonlinestoreee.orders.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
