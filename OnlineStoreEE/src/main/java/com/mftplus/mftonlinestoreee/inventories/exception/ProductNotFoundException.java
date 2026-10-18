package com.mftplus.mftonlinestoreee.inventories.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
