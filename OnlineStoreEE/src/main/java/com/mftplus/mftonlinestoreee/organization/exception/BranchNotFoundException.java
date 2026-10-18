package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class BranchNotFoundException extends BusinessException {

    public BranchNotFoundException(Long branchId) {
        super("Branch " + branchId + " does not exist");
    }
}
