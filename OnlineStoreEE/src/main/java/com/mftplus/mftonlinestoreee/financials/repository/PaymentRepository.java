package com.mftplus.mftonlinestoreee.financials.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.financials.entity.Payment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class PaymentRepository extends BaseRepository<Payment> {

    public PaymentRepository() {
    }

    public PaymentRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Payment> entityType() {
        return Payment.class;
    }


    public List<Payment> findByOrder(Long orderId) {
        return entityManager
                .createQuery("select p from Payment p where p.orderId = :orderId order by p.id", Payment.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}
