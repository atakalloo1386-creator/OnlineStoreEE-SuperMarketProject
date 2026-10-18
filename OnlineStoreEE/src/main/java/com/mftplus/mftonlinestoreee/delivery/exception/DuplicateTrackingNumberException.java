package com.mftplus.mftonlinestoreee.delivery.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateTrackingNumberException extends BusinessException {
    public DuplicateTrackingNumberException(String message) {
        super("The tracking Number " + message + " is already taken");
    }
}
