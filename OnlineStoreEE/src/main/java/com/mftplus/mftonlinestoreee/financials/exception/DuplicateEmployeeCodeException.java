package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateEmployeeCodeException extends BusinessException {

    public DuplicateEmployeeCodeException(String employeeCode) {
        super("Another cashier already has the employee bankCode '" + employeeCode + "'");
    }
}
