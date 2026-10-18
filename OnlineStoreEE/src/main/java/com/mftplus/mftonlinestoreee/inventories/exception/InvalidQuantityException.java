package com.mftplus.mftonlinestoreee.inventories.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class InvalidQuantityException extends BusinessException {
    public InvalidQuantityException(String message) {
        super(message);
    }
}
