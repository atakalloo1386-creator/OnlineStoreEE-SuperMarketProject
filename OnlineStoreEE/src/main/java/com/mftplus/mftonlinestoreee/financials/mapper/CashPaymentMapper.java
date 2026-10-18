package com.mftplus.mftonlinestoreee.financials.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.financials.dto.cashpayment.CashPaymentRequest;
import com.mftplus.mftonlinestoreee.financials.dto.cashpayment.CashPaymentDto;
import com.mftplus.mftonlinestoreee.financials.entity.CashPayment;
import org.mapstruct.Mapper;


@Mapper(config = MapStructConfig.class)
public interface CashPaymentMapper extends BaseMapper<CashPayment, CashPaymentDto, CashPaymentRequest> {

}
