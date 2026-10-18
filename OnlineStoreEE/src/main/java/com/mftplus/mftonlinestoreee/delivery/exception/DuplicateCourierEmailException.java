package com.mftplus.mftonlinestoreee.delivery.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateCourierEmailException extends BusinessException {
    public DuplicateCourierEmailException(String message) {
        super("Email " + message + " is already taken");
    }
}
