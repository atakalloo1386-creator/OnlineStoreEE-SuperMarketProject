package com.mftplus.mftonlinestoreee.orders.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class InvalidOrderStatusException extends BusinessException {
    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
