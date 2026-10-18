package com.mftplus.mftonlinestoreee.inventories.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
