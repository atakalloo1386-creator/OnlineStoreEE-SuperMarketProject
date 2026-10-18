package com.mftplus.mftonlinestoreee.financials.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.financials.dto.cardpayment.CardPaymentRequest;
import com.mftplus.mftonlinestoreee.financials.dto.cardpayment.CardPaymentDto;
import com.mftplus.mftonlinestoreee.financials.entity.CardPayment;
import org.mapstruct.Mapper;


@Mapper(config = MapStructConfig.class)
public interface CardPaymentMapper extends BaseMapper<CardPayment, CardPaymentDto, CardPaymentRequest> {
}
