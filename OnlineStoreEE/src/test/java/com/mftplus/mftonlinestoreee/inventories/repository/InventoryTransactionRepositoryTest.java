package com.mftplus.mftonlinestoreee.inventories.repository;

import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryTransaction;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryReferenceType;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryTransactionType;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTransactionRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    private InventoryTransactionRepository repository;

    private InventoryItem itemA;
    private InventoryItem itemB;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("testPU");
        em = emf.createEntityManager();

        repository = new InventoryTransactionRepository(em);

        em.getTransaction().begin();

        Inventory inventory = Inventory.builder()
                .name("Main Warehouse")
                .location("Tehran")
                .build();

        Product productA = Product.builder()
                .name("Product A")
                .build();

        Product productB = Product.builder()
                .name("Product B")
                .build();

        em.persist(inventory);
        em.persist(productA);
        em.persist(productB);

        itemA = InventoryItem.builder()
                .inventory(inventory)
                .product(productA)
                .quantity(10)
                .minimumQuantity(2)
                .batchNumber("BATCH-001")
                .build();

        itemB = InventoryItem.builder()
                .inventory(inventory)
                .product(productB)
                .quantity(20)
                .minimumQuantity(3)
                .batchNumber("BATCH-002")
                .build();

        em.persist(itemA);
        em.persist(itemB);

        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    void saveAndFindById() {
        InventoryTransaction saved = save(
                itemA,
                InventoryTransactionType.IN,
                5,
                "Initial stock"
        );

        Optional<InventoryTransaction> found =
                repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(5, found.get().getQuantity());
        assertEquals(
                InventoryTransactionType.IN,
                found.get().getTransactionType()
        );
        assertEquals(
                "Initial stock",
                found.get().getReason()
        );
    }

    @Test
    void findByItem() {
        save(itemA, InventoryTransactionType.IN, 10, "Stock received");
        save(itemA, InventoryTransactionType.OUT, 3, "Stock dispatched");
        save(itemB, InventoryTransactionType.IN, 20, "Other product stock");

        List<InventoryTransaction> found =
                repository.findByItem(itemA.getId());

        assertEquals(2, found.size());

        assertTrue(found.stream().allMatch(
                transaction -> transaction.getInventoryItem().getId()
                        .equals(itemA.getId())
        ));

        List<String> reasons = found.stream()
                .map(InventoryTransaction::getReason)
                .collect(Collectors.toList());

        assertEquals(
                List.of("Stock received", "Stock dispatched"),
                reasons
        );
    }

    @Test
    void findByItemReturnsEmptyListWhenNoTransactionsExist() {
        List<InventoryTransaction> found =
                repository.findByItem(itemA.getId());

        assertTrue(found.isEmpty());
    }

    @Test
    void deletedRowsAreNotFound() {
        InventoryTransaction saved = save(
                itemA,
                InventoryTransactionType.IN,
                5,
                "Stock received"
        );

        em.getTransaction().begin();
        repository.delete(saved.getId());
        em.getTransaction().commit();

        assertTrue(repository.findById(saved.getId()).isEmpty());
        assertTrue(repository.findByItem(itemA.getId()).isEmpty());
        assertEquals(0, repository.count());
    }

    private InventoryTransaction save(
            InventoryItem item,
            InventoryTransactionType type,
            int quantity,
            String reason) {

        em.getTransaction().begin();

        InventoryTransaction saved = repository.save(
                InventoryTransaction.builder()
                        .inventoryItem(item)
                        .transactionType(type)
                        .quantity(quantity)
                        .referenceType(InventoryReferenceType.MANUAL)
                        .referenceId(null)
                        .reason(reason)
                        .build()
        );

        em.getTransaction().commit();
        return saved;
    }
}