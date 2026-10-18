package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class EmployeeNotFoundException extends BusinessException {

    public EmployeeNotFoundException(Long userId) {
        super("User " + userId + " does not exist");
    }
}
