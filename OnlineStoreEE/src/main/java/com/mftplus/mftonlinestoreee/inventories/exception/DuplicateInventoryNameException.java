package com.mftplus.mftonlinestoreee.inventories.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateInventoryNameException extends BusinessException {
    public DuplicateInventoryNameException(String message) {
        super(message);
    }
}
