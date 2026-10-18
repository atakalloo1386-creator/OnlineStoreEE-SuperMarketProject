package com.mftplus.mftonlinestoreee.users.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateRoleNameException extends BusinessException {
    public DuplicateRoleNameException(String roleName) {
        super("Role name already exists: "+ roleName);
    }
}
