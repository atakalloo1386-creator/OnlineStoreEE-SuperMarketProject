package com.mftplus.mftonlinestoreee.financials.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.financials.entity.CardPayment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class CardPaymentRepository extends BaseRepository<CardPayment> {

    public CardPaymentRepository() {
    }

    public CardPaymentRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<CardPayment> entityType() {
        return CardPayment.class;
    }
}
