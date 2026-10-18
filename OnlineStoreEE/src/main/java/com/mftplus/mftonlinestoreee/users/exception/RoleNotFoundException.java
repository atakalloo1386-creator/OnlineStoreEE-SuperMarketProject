package com.mftplus.mftonlinestoreee.users.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class RoleNotFoundException extends BusinessException {

    public RoleNotFoundException(Long roleId) {
        super("Role " + roleId + " does not exist");
    }
}
