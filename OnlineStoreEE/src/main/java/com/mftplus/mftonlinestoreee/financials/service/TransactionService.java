package com.mftplus.mftonlinestoreee.financials.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.financials.dto.transaction.TransactionDto;
import com.mftplus.mftonlinestoreee.financials.dto.transaction.TransactionRequest;

import java.util.List;
import java.util.Optional;

public interface TransactionService extends BaseService<TransactionRequest, TransactionDto> {

    List<TransactionDto> findByBankAccount(Long bankAccountId);


    Optional<TransactionDto> register(Long bankAccountId, TransactionRequest request);
}
