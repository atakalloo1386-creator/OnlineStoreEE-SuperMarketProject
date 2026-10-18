package com.mftplus.mftonlinestoreee.financials.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.financials.dto.payment.PaymentDto;
import com.mftplus.mftonlinestoreee.financials.dto.payment.PaymentRequest;
import com.mftplus.mftonlinestoreee.financials.entity.Payment;
import org.mapstruct.Mapper;


@Mapper(config = MapStructConfig.class)
public interface PaymentMapper extends BaseMapper<Payment, PaymentDto, PaymentRequest> {


}
