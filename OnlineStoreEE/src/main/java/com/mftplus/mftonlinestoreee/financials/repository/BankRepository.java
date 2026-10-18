package com.mftplus.mftonlinestoreee.financials.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.financials.entity.Bank;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.Optional;

@ApplicationScoped
public class BankRepository extends BaseRepository<Bank> {

    public BankRepository() {
    }

    public BankRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Bank> entityType() {
        return Bank.class;
    }

    public Optional<Bank> findByCode(String code) {
        return findOneBy("bankCode", code);
    }
}
