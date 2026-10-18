package com.mftplus.mftonlinestoreee.financials.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.financials.dto.bank.BankDto;
import com.mftplus.mftonlinestoreee.financials.dto.bank.BankRequest;
import com.mftplus.mftonlinestoreee.financials.entity.Bank;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface BankMapper extends BaseMapper<Bank, BankDto, BankRequest> {
}
