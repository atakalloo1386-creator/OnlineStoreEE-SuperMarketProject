package com.mftplus.mftonlinestoreee.financials.mapper;

import com.mftplus.mftonlinestoreee.core.mapper.BaseMapper;
import com.mftplus.mftonlinestoreee.core.mapper.MapStructConfig;
import com.mftplus.mftonlinestoreee.financials.dto.transaction.TransactionDto;
import com.mftplus.mftonlinestoreee.financials.dto.transaction.TransactionRequest;
import com.mftplus.mftonlinestoreee.financials.entity.Transaction;
import org.mapstruct.Mapper;


@Mapper(config = MapStructConfig.class)
public interface TransactionMapper extends BaseMapper<Transaction, TransactionDto, TransactionRequest> {

}
