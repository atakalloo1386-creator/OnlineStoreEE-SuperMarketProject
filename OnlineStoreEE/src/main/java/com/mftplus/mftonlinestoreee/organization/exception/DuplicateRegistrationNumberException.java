package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateRegistrationNumberException extends BusinessException {

    public DuplicateRegistrationNumberException(String registrationNumber) {
        super("An organization with registration number '" + registrationNumber + "' already exists");
    }
}
