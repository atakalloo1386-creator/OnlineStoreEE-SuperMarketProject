package com.mftplus.mftonlinestoreee.financials.service;

import com.mftplus.mftonlinestoreee.core.service.BaseService;
import com.mftplus.mftonlinestoreee.financials.dto.bankaccount.BankAccountDto;
import com.mftplus.mftonlinestoreee.financials.dto.bankaccount.BankAccountRequest;

import java.util.List;

public interface BankAccountService extends BaseService<BankAccountRequest, BankAccountDto> {

    List<BankAccountDto> findByBank(Long bankId);
}
