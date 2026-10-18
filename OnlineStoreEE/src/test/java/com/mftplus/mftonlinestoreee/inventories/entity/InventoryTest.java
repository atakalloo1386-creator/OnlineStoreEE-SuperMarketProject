package com.mftplus.mftonlinestoreee.inventories.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

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
        Inventory inventory = inventory();

        persist(inventory);

        assertNotNull(inventory.getId());
        assertNotNull(inventory.getCreatedAt());
        assertEquals(0L, inventory.getVersion());
        assertFalse(inventory.isDeleted());
    }

    @Test
    void nameAndLocationAreStored() {
        Inventory inventory = inventory();

        persist(inventory);
        em.clear();

        Inventory found = em.find(Inventory.class, inventory.getId());

        assertNotNull(found);
        assertEquals("Main Warehouse", found.getName());
        assertEquals("Tehran", found.getLocation());
    }

    @Test
    void nameIsRequired() {
        Inventory inventory = inventory();
        inventory.setName(null);

        em.getTransaction().begin();

        assertThrows(PersistenceException.class, () -> {
            em.persist(inventory);
            em.flush();
        });

        em.getTransaction().rollback();
    }

    @Test
    void locationIsRequired() {
        Inventory inventory = inventory();
        inventory.setLocation(null);

        em.getTransaction().begin();

        assertThrows(PersistenceException.class, () -> {
            em.persist(inventory);
            em.flush();
        });

        em.getTransaction().rollback();
    }

    @Test
    void removeOnlyMarksTheRowAsDeleted() {
        Inventory inventory = inventory();

        persist(inventory);

        Long id = inventory.getId();

        em.getTransaction().begin();
        em.remove(inventory);
        em.getTransaction().commit();

        em.clear();

        assertNull(
                em.find(Inventory.class, id),
                "@SQLRestriction hides the deleted row"
        );

        Object deleted = em
                .createNativeQuery(
                        "select deleted from inventories where id = " + id
                )
                .getSingleResult();

        assertEquals(
                true,
                deleted,
                "@SQLDelete kept the row and only set deleted = true"
        );
    }

    private Inventory inventory() {
        return Inventory.builder()
                .name("Main Warehouse")
                .location("Tehran")
                .build();
    }

    private void persist(Inventory inventory) {
        em.getTransaction().begin();
        em.persist(inventory);
        em.getTransaction().commit();
    }
}