package com.mftplus.mftonlinestoreee.inventories.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class DuplicateInventoryItemException extends BusinessException {
    public DuplicateInventoryItemException(String message) {
        super(message);
    }
}
