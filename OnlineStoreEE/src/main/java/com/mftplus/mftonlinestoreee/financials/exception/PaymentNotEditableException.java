package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentStatus;

public class PaymentNotEditableException extends BusinessException {

    public PaymentNotEditableException(Long paymentId, PaymentStatus status) {
        super("Payment " + paymentId + " is " + status + ": only a PENDING payment can be changed or deleted");
    }
}
