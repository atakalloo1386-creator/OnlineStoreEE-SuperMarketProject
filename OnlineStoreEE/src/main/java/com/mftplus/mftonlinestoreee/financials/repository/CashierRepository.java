package com.mftplus.mftonlinestoreee.financials.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.financials.entity.Cashier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.Optional;

@ApplicationScoped
public class CashierRepository extends BaseRepository<Cashier> {

    public CashierRepository() {
    }

    public CashierRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Cashier> entityType() {
        return Cashier.class;
    }

    public Optional<Cashier> findByEmployeeCode(String employeeCode) {
        return findOneBy("employeeCode", employeeCode);
    }
}
