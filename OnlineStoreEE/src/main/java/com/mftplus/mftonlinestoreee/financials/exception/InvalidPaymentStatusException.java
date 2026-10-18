package com.mftplus.mftonlinestoreee.financials.exception;

import com.mftplus.mftonlinestoreee.core.exception.BusinessException;

import com.mftplus.mftonlinestoreee.financials.entity.enums.PaymentStatus;

public class InvalidPaymentStatusException extends BusinessException {

    public InvalidPaymentStatusException(Long paymentId, PaymentStatus status) {
        super("Cheque " + paymentId + " is " + status + ": only a PENDING cheque can be cleared or bounced");
    }
}
