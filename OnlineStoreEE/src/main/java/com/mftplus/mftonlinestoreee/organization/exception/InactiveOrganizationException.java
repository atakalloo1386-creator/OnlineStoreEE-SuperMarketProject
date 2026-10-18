package com.mftplus.mftonlinestoreee.organization.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class InactiveOrganizationException extends BusinessException {

    public InactiveOrganizationException(Long organizationId) {
        super("Organization " + organizationId + " is not active");
    }
}
