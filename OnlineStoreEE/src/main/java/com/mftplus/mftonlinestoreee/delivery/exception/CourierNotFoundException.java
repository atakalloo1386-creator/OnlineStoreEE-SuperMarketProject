package com.mftplus.mftonlinestoreee.delivery.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class CourierNotFoundException extends BusinessException {
    public CourierNotFoundException(Long message) {
        super("Courier with id " + message + " does not exist");
    }
}
