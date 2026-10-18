package com.mftplus.mftonlinestoreee.inventories.repository;

import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InventoryItemRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    private InventoryItemRepository repository;

    private Inventory inventoryA;
    private Inventory inventoryB;

    private Product productA;
    private Product productB;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("testPU");
        em = emf.createEntityManager();

        repository = new InventoryItemRepository(em);

        em.getTransaction().begin();

        inventoryA = Inventory.builder()
                .name("Warehouse A")
                .location("Tehran")
                .build();

        inventoryB = Inventory.builder()
                .name("Warehouse B")
                .location("Karaj")
                .build();

        productA = Product.builder()
                .name("Product A")
                .build();

        productB = Product.builder()
                .name("Product B")
                .build();

        em.persist(inventoryA);
        em.persist(inventoryB);
        em.persist(productA);
        em.persist(productB);

        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    void saveAndFindById() {
        InventoryItem saved = save(
                inventoryA, productA, 10, 2, "BATCH-001"
        );

        Optional<InventoryItem> found =
                repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(10, found.get().getQuantity());
        assertEquals(2, found.get().getMinimumQuantity());
        assertEquals("BATCH-001", found.get().getBatchNumber());
    }

    @Test
    void findByInventory() {
        save(inventoryA, productA, 10, 2, "BATCH-001");
        save(inventoryA, productB, 5, 2, "BATCH-002");
        save(inventoryB, productA, 8, 2, "BATCH-003");

        List<InventoryItem> found =
                repository.findByInventory(inventoryA.getId());

        assertEquals(2, found.size());

        assertTrue(found.stream().allMatch(
                item -> item.getInventory().getId()
                        .equals(inventoryA.getId())
        ));
    }

    @Test
    void findByInventoryAndProduct() {
        InventoryItem saved = save(
                inventoryA, productA, 10, 2, "BATCH-001"
        );

        Optional<InventoryItem> found =
                repository.findByInventoryAndProduct(
                        inventoryA.getId(),
                        productA.getId()
                );

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());

        assertTrue(
                repository.findByInventoryAndProduct(
                        inventoryB.getId(),
                        productA.getId()
                ).isEmpty(),
                "The product exists, but in another inventory"
        );

        assertTrue(
                repository.findByInventoryAndProduct(
                        inventoryA.getId(),
                        productB.getId()
                ).isEmpty()
        );
    }

    @Test
    void findLowStock() {
        save(inventoryA, productA, 2, 5, "BATCH-001");

        save(inventoryA, productB, 5, 5, "BATCH-002");

        List<InventoryItem> found = repository.findLowStock();

        assertEquals(1, found.size());
        assertEquals(2, found.get(0).getQuantity());
        assertEquals(5, found.get(0).getMinimumQuantity());
    }

    @Test
    void countByInventory() {
        save(inventoryA, productA, 10, 2, "BATCH-001");
        save(inventoryA, productB, 5, 2, "BATCH-002");
        save(inventoryB, productA, 8, 2, "BATCH-003");

        assertEquals(2, repository.countByInventory(inventoryA.getId()));
        assertEquals(1, repository.countByInventory(inventoryB.getId()));
    }

    @Test
    void deletedRowsAreNotFound() {
        InventoryItem saved = save(
                inventoryA, productA, 10, 2, "BATCH-001"
        );

        em.getTransaction().begin();
        repository.delete(saved.getId());
        em.getTransaction().commit();

        assertTrue(repository.findById(saved.getId()).isEmpty());

        assertTrue(
                repository.findByInventoryAndProduct(
                        inventoryA.getId(),
                        productA.getId()
                ).isEmpty()
        );

        assertEquals(0, repository.countByInventory(inventoryA.getId()));
    }

    private InventoryItem save(
            Inventory inventory,
            Product product,
            int quantity,
            int minimumQuantity,
            String batchNumber) {

        em.getTransaction().begin();

        InventoryItem saved = repository.save(
                InventoryItem.builder()
                        .inventory(inventory)
                        .product(product)
                        .quantity(quantity)
                        .minimumQuantity(minimumQuantity)
                        .batchNumber(batchNumber)
                        .build()
        );

        em.getTransaction().commit();
        return saved;
    }
}