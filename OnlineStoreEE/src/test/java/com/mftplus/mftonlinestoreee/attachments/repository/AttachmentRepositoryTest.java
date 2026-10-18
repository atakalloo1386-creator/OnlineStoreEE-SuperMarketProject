package com.mftplus.mftonlinestoreee.attachments.repository;

import com.mftplus.mftonlinestoreee.attachments.entity.Attachment;
import com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType;
import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType.PRODUCT;
import static com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType.USER;
import static org.junit.jupiter.api.Assertions.*;

class AttachmentRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private AttachmentRepository repository;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("testPU");
        em = emf.createEntityManager();
        // No CDI outside the server, so the EntityManager is handed to the repository by hand
        repository = new AttachmentRepository(em);
    }

    @AfterEach
    void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    void saveAndFindById() {
        Attachment saved = save(PRODUCT, 5L, "a.pdf");

        Optional<Attachment> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("a.pdf", found.get().getFileName());
    }

    @Test
    void findByOwner() {
        save(PRODUCT, 5L, "a.pdf");
        save(PRODUCT, 5L, "b.pdf");
        save(PRODUCT, 6L, "c.pdf");
        save(USER, 5L, "d.pdf");

        List<Attachment> found = repository.findByOwner(PRODUCT, 5L);

        assertEquals(List.of("a.pdf", "b.pdf"), found.stream().map(Attachment::getFileName).toList());
    }

    @Test
    void findByOwnerAndFileName() {
        save(PRODUCT, 5L, "a.pdf");

        assertTrue(repository.findByOwnerAndFileName(PRODUCT, 5L, "a.pdf").isPresent());
        assertTrue(repository.findByOwnerAndFileName(PRODUCT, 6L, "a.pdf").isEmpty(), "another owner");
        assertTrue(repository.findByOwnerAndFileName(PRODUCT, 5L, "missing.pdf").isEmpty());
    }

    @Test
    void deletedRowsAreNotFound() {
        Attachment saved = save(PRODUCT, 5L, "a.pdf");

        em.getTransaction().begin();
        repository.delete(saved.getId());
        em.getTransaction().commit();

        assertTrue(repository.findById(saved.getId()).isEmpty());
        assertTrue(repository.findByOwner(PRODUCT, 5L).isEmpty());
        assertEquals(0, repository.count());
    }

    @Test
    void findPage() {
        save(PRODUCT, 5L, "1.pdf");
        save(PRODUCT, 5L, "2.pdf");
        save(PRODUCT, 5L, "3.pdf");

        PageResult<Attachment> page = repository.findPage(1, 2);

        assertEquals(1, page.content().size());
        assertEquals("3.pdf", page.content().get(0).getFileName());
        assertEquals(3, page.totalElements());
        assertEquals(2, page.totalPages());
    }

    private Attachment save(AttachmentOwnerType ownerType, Long ownerId, String fileName) {
        em.getTransaction().begin();
        Attachment saved = repository.save(Attachment.builder()
                .ownerType(ownerType)
                .ownerId(ownerId)
                .title("title")
                .fileName(fileName)
                .build());
        em.getTransaction().commit();
        return saved;
    }
}
