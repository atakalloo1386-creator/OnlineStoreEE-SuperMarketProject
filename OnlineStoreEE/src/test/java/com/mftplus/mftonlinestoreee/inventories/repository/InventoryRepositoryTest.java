package com.mftplus.mftonlinestoreee.inventories.repository;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
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

class InventoryRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private InventoryRepository repository;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("testPU");
        em = emf.createEntityManager();

        repository = new InventoryRepository(em);
    }

    @AfterEach
    void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    void saveAndFindById() {
        Inventory saved = save("Main Warehouse", "Tehran");

        Optional<Inventory> found =
                repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Main Warehouse", found.get().getName());
        assertEquals("Tehran", found.get().getLocation());
    }

    @Test
    void findByName() {
        save("Main Warehouse", "Tehran");
        save("Secondary Warehouse", "Karaj");

        Optional<Inventory> found =
                repository.findByName("Main Warehouse");

        assertTrue(found.isPresent());
        assertEquals("Tehran", found.get().getLocation());

        assertTrue(repository.findByName("Missing Warehouse").isEmpty());
    }

    @Test
    void deletedRowsAreNotFound() {
        Inventory saved = save("Main Warehouse", "Tehran");

        em.getTransaction().begin();
        repository.delete(saved.getId());
        em.getTransaction().commit();

        assertTrue(repository.findById(saved.getId()).isEmpty());
        assertTrue(repository.findByName("Main Warehouse").isEmpty());
        assertEquals(0, repository.count());
    }

    @Test
    void findPage() {
        save("Warehouse A", "Tehran");
        save("Warehouse B", "Karaj");
        save("Warehouse C", "Qazvin");

        PageResult<Inventory> page = repository.findPage(1, 2);

        assertEquals(1, page.content().size());
        assertEquals("Warehouse C", page.content().get(0).getName());
        assertEquals(3, page.totalElements());
        assertEquals(2, page.totalPages());
    }

    @Test
    void findAllReturnsSavedInventories() {
        save("Warehouse A", "Tehran");
        save("Warehouse B", "Karaj");

        List<String> names = repository.findAll()
                .stream()
                .map(Inventory::getName)
                .collect(Collectors.toList());

        assertEquals(
                List.of("Warehouse A", "Warehouse B"),
                names
        );
    }

    private Inventory save(String name, String location) {
        em.getTransaction().begin();

        Inventory saved = repository.save(
                Inventory.builder()
                        .name(name)
                        .location(location)
                        .build()
        );

        em.getTransaction().commit();
        return saved;
    }
}
