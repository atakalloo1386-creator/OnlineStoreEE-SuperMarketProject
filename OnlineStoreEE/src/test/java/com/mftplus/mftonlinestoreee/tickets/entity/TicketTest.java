package com.mftplus.mftonlinestoreee.tickets.entity;

import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;
import com.mftplus.mftonlinestoreee.tickets.entity.testutil.TestDataFactory;
import com.mftplus.mftonlinestoreee.users.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    private User user = TestDataFactory.createUser();

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("testPU");
        em = emf.createEntityManager();
    }

    @AfterEach
    void tearDown(){
        em.close();
        emf.close();
    }

    @Test
    void persistTicketIdCreatedAtAndVersion(){
        Ticket ticket = invoice();

        persist(ticket);

        assertNotNull(ticket.getId());
        assertNotNull(ticket.getCreatedAt());
        assertEquals(0L, ticket.getVersion());
        assertFalse(ticket.isDeleted());
    }

    @Test
    void ticketStatusIsStoredAsText() {
        Ticket ticket = invoice();
        persist(ticket);

        Object ticketStatus = em.createNativeQuery("select status from tickets where id = " + ticket.getId())
                .getSingleResult();

        assertEquals("IN_PROGRESS", ticketStatus, "@Enumerated(EnumType.String) stores the name, not a number");
    }

    @Test
    void removeOnlyMarksTheRowAsDeleted(){
        Ticket ticket = invoice();
        persist(ticket);
        Long id = ticket.getId();

        em.getTransaction().begin();
        em.remove(ticket);
        em.getTransaction().commit();
        em.clear();

        assertNull(em.find(Ticket.class, id), "SQLRestriction hides the deleted row");
        Object deleted = em.createNativeQuery("select deleted from tickets where id = " + id)
                .getSingleResult();
        assertEquals(true, deleted, "SQLDelete kept the row and only set deleted = true");
    }

    @Test
    void subjectIsRequired(){
        Ticket ticket = invoice();
        ticket.setSubject(null);

        em.getTransaction().begin();
        assertThrows(PersistenceException.class, () -> {
            em.persist(ticket);
            em.flush();
        });
        em.getTransaction().rollback();
    }

    @Test
    void  subject150CharsExceeded(){
        Ticket ticket = invoice();
        ticket.setSubject("a".repeat(151));

        em.getTransaction().begin();
        assertThrows(PersistenceException.class, () -> {
            em.persist(ticket);
            em.flush();
        });
        em.getTransaction().rollback();
    }

    private Ticket invoice(){
        return Ticket
                .builder()
                .subject("Ticket")
                .user(user)
                .status(TicketStatus.IN_PROGRESS)
                .build();
    }

    private void persist(Ticket ticket){
        em.getTransaction().begin();
        if (ticket.getUser().getId() == null){
            em.persist(ticket.getUser());
        }
        em.persist(ticket);
        em.getTransaction().commit();
    }
}
