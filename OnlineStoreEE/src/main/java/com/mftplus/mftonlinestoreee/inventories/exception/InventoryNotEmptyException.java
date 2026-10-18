package com.mftplus.mftonlinestoreee.inventories.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class InventoryNotEmptyException extends BusinessException {
    public InventoryNotEmptyException(String message) {
        super(message);
    }
}
