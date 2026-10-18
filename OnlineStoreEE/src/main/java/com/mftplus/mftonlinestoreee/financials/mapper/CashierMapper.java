package com.mftplus.mftonlinestoreee.financials.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.financials.dto.cashier.CashierDto;
import com.mftplus.mftonlinestoreee.financials.dto.cashier.CashierRequest;
import com.mftplus.mftonlinestoreee.financials.entity.Cashier;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CashierMapper extends BaseMapper<Cashier, CashierDto, CashierRequest> {
}
