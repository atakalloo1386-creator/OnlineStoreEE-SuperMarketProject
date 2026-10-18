package com.mftplus.mftonlinestoreee.users.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class RoleInUseException extends BusinessException {
    public RoleInUseException(String roleName) {
        super("Cannot delete role because it is assigned to one or more users:  " + roleName);
    }
}
