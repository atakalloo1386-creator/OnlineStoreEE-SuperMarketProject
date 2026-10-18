package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateBranchCodeException extends BusinessException {

    public DuplicateBranchCodeException(String branchCode) {
        super("A branch with code '" + branchCode + "' already exists");
    }
}
