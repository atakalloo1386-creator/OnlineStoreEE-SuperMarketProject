package com.mftplus.mftonlinestoreee.inventories.entity;

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

class InventoryItemTest {

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
        Inventory inventory = persistInventory();
        Product product = persistProduct();

        InventoryItem item = inventoryItem(inventory, product);

        persist(item);

        assertNotNull(item.getId());
        assertNotNull(item.getCreatedAt());
        assertEquals(0L, item.getVersion());
        assertFalse(item.isDeleted());
    }

    @Test
    void quantityIsStored() {
        Inventory inventory = persistInventory();
        Product product = persistProduct();

        InventoryItem item = inventoryItem(inventory, product);
        persist(item);

        em.clear();

        InventoryItem found =
                em.find(InventoryItem.class, item.getId());

        assertNotNull(found);
        assertEquals(10, found.getQuantity());
        assertEquals(2, found.getMinimumQuantity());
    }

    @Test
    void expirationDateCanBeNull() {
        Inventory inventory = persistInventory();
        Product product = persistProduct();

        InventoryItem item = InventoryItem.builder()
                .inventory(inventory)
                .product(product)
                .quantity(10)
                .minimumQuantity(2)
                .batchNumber("BATCH-001")
                .expirationDate(null)
                .build();

        persist(item);

        assertNotNull(item.getId());
        assertNull(item.getExpirationDate());
    }

    @Test
    void inventoryAndProductRelationshipIsStored() {
        Inventory inventory = persistInventory();
        Product product = persistProduct();

        InventoryItem item = inventoryItem(inventory, product);

        persist(item);

        em.clear();

        InventoryItem found =
                em.find(InventoryItem.class, item.getId());

        assertNotNull(found);
        assertEquals(
                inventory.getId(),
                found.getInventory().getId()
        );
        assertEquals(
                product.getId(),
                found.getProduct().getId()
        );
    }

    @Test
    void duplicateInventoryAndProductIsRejected() {
        Inventory inventory = persistInventory();
        Product product = persistProduct();

        InventoryItem first =
                inventoryItem(inventory, product);

        persist(first);

        InventoryItem second =
                inventoryItem(inventory, product);

        em.getTransaction().begin();

        assertThrows(PersistenceException.class, () -> {
            em.persist(second);
            em.flush();
        });

        em.getTransaction().rollback();
    }

    @Test
    void removeOnlyMarksTheRowAsDeleted() {
        Inventory inventory = persistInventory();
        Product product = persistProduct();

        InventoryItem item =
                inventoryItem(inventory, product);

        persist(item);

        Long id = item.getId();

        em.getTransaction().begin();
        em.remove(item);
        em.getTransaction().commit();

        em.clear();

        assertNull(
                em.find(InventoryItem.class, id),
                "@SQLRestriction hides the deleted row"
        );

        Object deleted = em
                .createNativeQuery(
                        "select deleted from inventory_items where id = " + id
                )
                .getSingleResult();

        assertEquals(true, deleted);
    }

    private InventoryItem inventoryItem(
            Inventory inventory,
            Product product) {

        return InventoryItem.builder()
                .inventory(inventory)
                .product(product)
                .quantity(10)
                .minimumQuantity(2)
                .batchNumber("BATCH-001")
                .expirationDate(LocalDate.now())
                .build();
    }

    private Inventory persistInventory() {

        Inventory inventory = Inventory.builder()
                .name("Main Warehouse")
                .location("Tehran")
                .build();

        em.getTransaction().begin();
        em.persist(inventory);
        em.getTransaction().commit();

        return inventory;
    }

    private Product persistProduct() {
        Product product = Product.builder()
                .name("Test Product")
                .build();

        em.getTransaction().begin();
        em.persist(product);
        em.getTransaction().commit();

        return product;
    }

    private void persist(InventoryItem item) {
        em.getTransaction().begin();
        em.persist(item);
        em.getTransaction().commit();
    }
}