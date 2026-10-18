package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class OrganizationNotFoundException extends BusinessException {

    public OrganizationNotFoundException(Long organizationId) {
        super("Organization " + organizationId + " does not exist");
    }
}
