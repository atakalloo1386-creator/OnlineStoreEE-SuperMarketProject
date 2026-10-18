package com.mftplus.mftonlinestoreee.financials.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.financials.entity.BankAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BankAccountRepository extends BaseRepository<BankAccount> {

    public BankAccountRepository() {
    }

    public BankAccountRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<BankAccount> entityType() {
        return BankAccount.class;
    }

    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return findOneBy("accountNumber", accountNumber);
    }

    public List<BankAccount> findByBank(Long bankId) {
        return entityManager
                .createQuery("select b from BankAccount b where b.bank.id = :bankId order by b.id", BankAccount.class)
                .setParameter("bankId", bankId)
                .getResultList();
    }

    public long countByBank(Long bankId) {
        return entityManager
                .createQuery("select count(b) from BankAccount b where b.bank.id = :bankId", Long.class)
                .setParameter("bankId", bankId)
                .getSingleResult();
    }


    public Optional<BankAccount> findByIdForUpdate(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return entityManager
                .createQuery("select ba from BankAccount ba where ba.id = :id", BankAccount.class)
                .setParameter("id", id)
//                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultStream()
                .findFirst();
    }
}
