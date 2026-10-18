package com.mftplus.mftonlinestoreee.financials.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.financials.entity.ChequePayment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class ChequePaymentRepository extends BaseRepository<ChequePayment> {

    public ChequePaymentRepository() {
    }

    public ChequePaymentRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<ChequePayment> entityType() {
        return ChequePayment.class;
    }
}
