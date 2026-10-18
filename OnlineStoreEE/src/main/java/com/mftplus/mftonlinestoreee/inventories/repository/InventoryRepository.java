package com.mftplus.mftonlinestoreee.inventories.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.Optional;

@ApplicationScoped
public class InventoryRepository extends BaseRepository<Inventory> {
    public InventoryRepository(){}

    public InventoryRepository(EntityManager entityManager) {super(entityManager);}

    @Override
    protected Class<Inventory> entityType() {return Inventory.class;}

    public Optional<Inventory> findByName(String name) {
        return findOneBy("name", name);
    }
}