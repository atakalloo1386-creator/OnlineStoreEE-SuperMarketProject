package com.mftplus.mftonlinestoreee.financials.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.financials.dto.payment.PaymentDto;
import com.mftplus.mftonlinestoreee.financials.dto.payment.PaymentRequest;

import java.util.List;

public interface PaymentService extends BaseService<PaymentRequest, PaymentDto> {


    List<PaymentDto> findByOrder(Long orderId);
}
