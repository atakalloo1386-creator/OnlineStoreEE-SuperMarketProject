package com.mftplus.mftonlinestoreee.inventories.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class InventoryNotFoundException extends BusinessException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
}
