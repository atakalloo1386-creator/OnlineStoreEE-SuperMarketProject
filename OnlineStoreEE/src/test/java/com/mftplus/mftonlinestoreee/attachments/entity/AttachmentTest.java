package com.mftplus.mftonlinestoreee.attachments.entity;

import com.mftplus.mftonlinestoreee.attachments.entity.enums.AttachmentOwnerType;
import com.mftplus.mftonlinestoreee.users.exception.DuplicateUsernameException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttachmentTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        // testPU = in-memory H2 (src/test/resources/META-INF/persistence.xml); tables are created fresh for every test
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
        Attachment attachment = invoice();

        persist(attachment);

        assertNotNull(attachment.getId());
        assertNotNull(attachment.getCreatedAt());
        assertEquals(0L, attachment.getVersion());
        assertFalse(attachment.isDeleted());
    }

    @Test
    void ownerTypeIsStoredAsText() {
        Attachment attachment = invoice();
        persist(attachment);

        Object ownerType = em.createNativeQuery("select ownerType from attachments where id = " + attachment.getId())
                .getSingleResult();

        assertEquals("PRODUCT", ownerType, "@Enumerated(EnumType.STRING) stores the name, not a number");
    }

    @Test
    void removeOnlyMarksTheRowAsDeleted() {
        Attachment attachment = invoice();
        persist(attachment);
        Long id = attachment.getId();

        em.getTransaction().begin();
        em.remove(attachment);
        em.getTransaction().commit();
        em.clear();

        assertNull(em.find(Attachment.class, id), "@SQLRestriction hides the deleted row");
        Object deleted = em.createNativeQuery("select deleted from attachments where id = " + id).getSingleResult();
        assertEquals(true, deleted, "@SQLDelete kept the row and only set deleted = true");
    }

    @Test
    void titleIsRequired() {
        Attachment attachment = invoice();
        attachment.setTitle(null);

        em.getTransaction().begin();
        assertThrows(PersistenceException.class, () -> {
            em.persist(attachment);
            em.flush();
        });
        em.getTransaction().rollback();
    }

    private Attachment invoice() {
        return Attachment.builder()
                .ownerType(AttachmentOwnerType.PRODUCT)
                .ownerId(5L)
                .title("Invoice")
                .fileName("a.pdf")
                .build();
    }

    private void persist(Attachment attachment) {
        em.getTransaction().begin();
        em.persist(attachment);
        em.getTransaction().commit();
    }
}
