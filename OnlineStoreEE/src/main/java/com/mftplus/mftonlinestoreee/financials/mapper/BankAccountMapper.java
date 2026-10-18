package com.mftplus.mftonlinestoreee.financials.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.financials.dto.bankaccount.BankAccountDto;
import com.mftplus.mftonlinestoreee.financials.dto.bankaccount.BankAccountRequest;
import com.mftplus.mftonlinestoreee.financials.entity.BankAccount;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface BankAccountMapper extends BaseMapper<BankAccount, BankAccountDto, BankAccountRequest> {

}
