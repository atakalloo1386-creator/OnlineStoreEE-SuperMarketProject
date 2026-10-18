package com.mftplus.mftonlinestoreee.financials.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.financials.entity.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class TransactionRepository extends BaseRepository<Transaction> {

    public TransactionRepository() {
    }

    public TransactionRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Transaction> entityType() {
        return Transaction.class;
    }

    public List<Transaction> findByBankAccount(Long bankAccountId) {
        return entityManager
                .createQuery("select t from Transaction t where t.bankAccount.id = :bankAccountId order by t.id",
                        Transaction.class)
                .setParameter("bankAccountId", bankAccountId)
                .getResultList();
    }
}
