package com.mftplus.mftonlinestoreee.financials.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.financials.dto.chequepayment.ChequePaymentRequest;
import com.mftplus.mftonlinestoreee.financials.dto.chequepayment.ChequePaymentDto;
import com.mftplus.mftonlinestoreee.financials.entity.ChequePayment;
import org.mapstruct.Mapper;


@Mapper(config = MapStructConfig.class)
public interface ChequePaymentMapper extends BaseMapper<ChequePayment, ChequePaymentDto, ChequePaymentRequest> {

}
