package com.mftplus.mftonlinestoreee.financials.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.financials.entity.CashPayment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class CashPaymentRepository extends BaseRepository<CashPayment> {

    public CashPaymentRepository() {
    }

    public CashPaymentRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<CashPayment> entityType() {
        return CashPayment.class;
    }
}
