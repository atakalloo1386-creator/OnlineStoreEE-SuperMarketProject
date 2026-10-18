package com.mftplus.mftonlinestoreee.orders.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateOrderItemException extends BusinessException {
    public DuplicateOrderItemException(String message) {
        super(message);
    }
}
