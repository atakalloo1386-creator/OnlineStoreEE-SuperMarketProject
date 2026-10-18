package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class InactiveBranchException extends BusinessException {

    public InactiveBranchException(Long branchId) {
        super("Branch " + branchId + " is not active");
    }
}
