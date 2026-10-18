package com.mftplus.mftonlinestoreee.orders.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

public class OrderNotEditableException extends BusinessException {
    public OrderNotEditableException(String message) {
        super("order number "+ message +"is not editable");
    }
}
