package com.mftplus.mftonlinestoreee.inventories.entity;

import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryReferenceType;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryTransactionType;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTransactionTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("testPU");
        em = emf.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    void persistFillsIdCreatedAtAndVersion() {

        InventoryItem item = persistInventoryItem();

        InventoryTransaction transaction =
                transaction(item);

        persist(transaction);

        assertNotNull(transaction.getId());
        assertNotNull(transaction.getCreatedAt());
        assertEquals(0L, transaction.getVersion());
        assertFalse(transaction.isDeleted());
    }

    @Test
    void transactionTypeIsStoredAsText() {

        InventoryItem item = persistInventoryItem();

        InventoryTransaction transaction =
                transaction(item);

        persist(transaction);

        Object type = em
                .createNativeQuery(
                        "select inventory_transaction_type " +
                                "from inventory_transactions where id = "
                                + transaction.getId()
                )
                .getSingleResult();

        assertEquals(
                "IN",
                type,
                "@Enumerated(EnumType.STRING) stores enum name"
        );
    }

    @Test
    void referenceTypeIsStoredAsText() {

        InventoryItem item = persistInventoryItem();

        InventoryTransaction transaction =
                transaction(item);

        persist(transaction);

        Object referenceType = em
                .createNativeQuery(
                        "select reference_type " +
                                "from inventory_transactions where id = "
                                + transaction.getId()
                )
                .getSingleResult();

        assertEquals("ORDER", referenceType);
    }

    @Test
    void referenceIdCanBeNull() {

        InventoryItem item = persistInventoryItem();

        InventoryTransaction transaction =
                InventoryTransaction.builder()
                        .quantity(5)
                        .transactionType(
                                InventoryTransactionType.IN
                        )
                        .referenceType(
                                InventoryReferenceType.MANUAL
                        )
                        .referenceId(null)
                        .reason("Manual stock entry")
                        .inventoryItem(item)
                        .build();

        persist(transaction);

        assertNotNull(transaction.getId());
        assertNull(transaction.getReferenceId());
    }

    @Test
    void transactionIsLinkedToInventoryItem() {

        InventoryItem item = persistInventoryItem();

        InventoryTransaction transaction =
                transaction(item);

        persist(transaction);

        em.clear();

        InventoryTransaction found =
                em.find(
                        InventoryTransaction.class,
                        transaction.getId()
                );

        assertNotNull(found);
        assertEquals(
                item.getId(),
                found.getInventoryItem().getId()
        );
    }

    @Test
    void reasonIsRequired() {

        InventoryItem item = persistInventoryItem();

        InventoryTransaction transaction =
                transaction(item);

        transaction.setReason(null);

        em.getTransaction().begin();

        assertThrows(PersistenceException.class, () -> {
            em.persist(transaction);
            em.flush();
        });

        em.getTransaction().rollback();
    }

    @Test
    void removeOnlyMarksTheRowAsDeleted() {

        InventoryItem item = persistInventoryItem();

        InventoryTransaction transaction =
                transaction(item);

        persist(transaction);

        Long id = transaction.getId();

        em.getTransaction().begin();
        em.remove(transaction);
        em.getTransaction().commit();

        em.clear();

        assertNull(
                em.find(InventoryTransaction.class, id),
                "@SQLRestriction hides the deleted row"
        );

        Object deleted = em
                .createNativeQuery(
                        "select deleted " +
                                "from inventory_transactions where id = "
                                + id
                )
                .getSingleResult();

        assertEquals(true, deleted);
    }

    private InventoryTransaction transaction(
            InventoryItem item) {

        return InventoryTransaction.builder()
                .quantity(5)
                .transactionType(
                        InventoryTransactionType.IN
                )
                .referenceType(
                        InventoryReferenceType.ORDER
                )
                .referenceId(100L)
                .reason("Purchase order")
                .inventoryItem(item)
                .build();
    }

    private InventoryItem persistInventoryItem() {

        Inventory inventory = Inventory.builder()
                .name("Main Warehouse")
                .location("Tehran")
                .build();

        Product product = Product.builder()
                .name("Test Product")
                .build();

        em.getTransaction().begin();

        em.persist(inventory);
        em.persist(product);

        em.getTransaction().commit();

        InventoryItem item = InventoryItem.builder()
                .inventory(inventory)
                .product(product)
                .quantity(10)
                .minimumQuantity(2)
                .batchNumber("BATCH-001")
                .expirationDate(LocalDate.now())
                .build();

        em.getTransaction().begin();
        em.persist(item);
        em.getTransaction().commit();

        return item;
    }

    private void persist(
            InventoryTransaction transaction) {

        em.getTransaction().begin();
        em.persist(transaction);
        em.getTransaction().commit();
    }
}