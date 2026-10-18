package com.mftplus.mftonlinestoreee.inventories.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class InventoryTransactionRepository extends BaseRepository<InventoryTransaction> {
    public InventoryTransactionRepository(){}

    public InventoryTransactionRepository(EntityManager entityManager) {super(entityManager);}

    @Override
    protected Class<InventoryTransaction> entityType() {return InventoryTransaction.class;}

    public List<InventoryTransaction> findByItem(Long itemId) {
        return entityManager
                .createQuery(
                        "select t from InventoryTransaction t" +
                                " where t.inventoryItem.id = :itemId" +
                                " order by t.id",
                        InventoryTransaction.class
                )
                .setParameter("itemId", itemId)
                .getResultList();
    }
}