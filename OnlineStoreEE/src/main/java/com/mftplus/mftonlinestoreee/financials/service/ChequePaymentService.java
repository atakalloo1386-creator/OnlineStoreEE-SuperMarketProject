package com.mftplus.mftonlinestoreee.financials.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.financials.dto.chequepayment.ChequePaymentRequest;
import com.mftplus.mftonlinestoreee.financials.dto.chequepayment.ChequePaymentDto;

import java.util.Optional;

public interface ChequePaymentService extends BaseService<ChequePaymentRequest, ChequePaymentDto> {


    Optional<ChequePaymentDto> clear(Long id);


    Optional<ChequePaymentDto> bounce(Long id);
}
