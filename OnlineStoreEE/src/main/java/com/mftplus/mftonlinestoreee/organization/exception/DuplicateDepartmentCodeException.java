package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateDepartmentCodeException extends BusinessException {

    public DuplicateDepartmentCodeException(String code) {
        super("This branch already has a department with code '" + code + "'");
    }
}
