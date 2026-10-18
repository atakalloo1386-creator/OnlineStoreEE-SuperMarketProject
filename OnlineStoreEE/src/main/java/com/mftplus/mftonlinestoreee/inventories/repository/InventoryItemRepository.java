package com.mftplus.mftonlinestoreee.inventories.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InventoryItemRepository extends  BaseRepository<InventoryItem> {
    public InventoryItemRepository(){}

    public InventoryItemRepository(EntityManager entityManager) {super(entityManager);}

    @Override
    protected Class<InventoryItem> entityType() {return InventoryItem.class;}

    public List<InventoryItem> findByInventory(Long inventoryId) {
        return entityManager
                .createQuery(
                        "select i from InventoryItem i" +
                                " where i.inventory.id = :inventoryId" +
                                " order by i.id",
                        InventoryItem.class
                )
                .setParameter("inventoryId", inventoryId)
                .getResultList();
    }

    public Optional<InventoryItem> findByInventoryAndProduct(Long inventoryId, Long productId) {
        return entityManager
                .createQuery(
                        "select i from InventoryItem i" +
                                " where i.inventory.id = :inventoryId" +
                                " and i.product.id = :productId",
                        InventoryItem.class
                )
                .setParameter("inventoryId", inventoryId)
                .setParameter("productId", productId)
                .getResultStream()
                .findFirst();
    }

    public List<InventoryItem> findLowStock() {
        return entityManager
                .createQuery(
                        "select i from InventoryItem i" +
                                " where i.quantity < i.minimumQuantity" +
                                " order by i.id",
                        InventoryItem.class
                )
                .getResultList();
    }

    public long countByInventory(Long inventoryId) {
        return entityManager
                .createQuery(
                        "select count(i) from InventoryItem i" +
                                " where i.inventory.id = :inventoryId",
                        Long.class
                )
                .setParameter("inventoryId", inventoryId)
                .getSingleResult();
    }
}