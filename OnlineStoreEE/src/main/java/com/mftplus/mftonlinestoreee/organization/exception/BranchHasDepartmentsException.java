package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class BranchHasDepartmentsException extends BusinessException {

    public BranchHasDepartmentsException(Long branchId) {
        super("Branch " + branchId + " still has departments");
    }
}
