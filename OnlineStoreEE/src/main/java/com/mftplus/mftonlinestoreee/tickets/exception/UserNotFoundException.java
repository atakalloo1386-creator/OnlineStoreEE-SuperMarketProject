package com.mftplus.mftonlinestoreee.tickets.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        this(userId, "User");
    }

    public UserNotFoundException(Long id, String type) {
        super(type + " with id " + id + " not found");
    }
}
