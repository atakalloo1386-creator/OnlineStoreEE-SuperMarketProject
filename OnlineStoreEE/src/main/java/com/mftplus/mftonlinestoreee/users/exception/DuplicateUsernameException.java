package com.mftplus.mftonlinestoreee.users.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateUsernameException extends BusinessException {

    public DuplicateUsernameException(String username) {
        super("The username '" + username + "' is already taken");
    }
}
