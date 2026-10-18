package com.mftplus.mftonlinestoreee.inventories.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class StockNotEmptyException extends BusinessException {
    public StockNotEmptyException(String message) {
        super(message);
    }
}
