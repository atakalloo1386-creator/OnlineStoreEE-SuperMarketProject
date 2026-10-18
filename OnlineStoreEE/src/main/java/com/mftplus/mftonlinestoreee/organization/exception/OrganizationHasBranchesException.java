package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class OrganizationHasBranchesException extends BusinessException {

    public OrganizationHasBranchesException(Long organizationId) {
        super("Organization " + organizationId + " still has branches");
    }
}
