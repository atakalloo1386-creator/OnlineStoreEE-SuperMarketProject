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

class TicketMessageTest {
    
    private EntityManagerFactory emf;
    private EntityManager em;

    private User user = TestDataFactory.createUser();

    private Ticket ticket = Ticket
            .builder()
            .subject("Transfer Issue")
            .user(user)
            .status(TicketStatus.IN_PROGRESS)
            .build();
    
    @BeforeEach
    void setUp(){
        emf = Persistence.createEntityManagerFactory("testPU");
        em = emf.createEntityManager();
    }
    
    @AfterEach
    void tearDown(){
        em.close();
        emf.close();
    }

    @Test
    void persistMessageIdCreatedAtAndVersion(){
        TicketMessage ticketMessage = invoice();

        persist(ticketMessage);

        assertNotNull(ticketMessage.getId());
        assertNotNull(ticketMessage.getCreatedAt());
        assertEquals(0L, ticketMessage.getVersion());
        assertFalse(ticketMessage.isDeleted());
    }

    @Test
    void removeOnlyMarksTheRowAsDeleted(){
        TicketMessage ticketMessage = invoice();
        persist(ticketMessage);
        Long id = ticketMessage.getId();

        em.getTransaction().begin();
        em.remove(ticketMessage);
        em.getTransaction().commit();
        em.clear();

        assertNull(em.find(TicketMessage.class, id), "SQLRestriction hides the deleted row");
        Object deleted = em.createNativeQuery("select deleted from ticket_messages where id = " + id)
                .getSingleResult();
        assertEquals(true, deleted, "SQLDelete kept the row and only set deleted = true");
    }

    @Test
    void contentIsRequired(){
        TicketMessage ticketMessage = invoice();
        ticketMessage.setContent(null);

        em.getTransaction().begin();
        assertThrows(PersistenceException.class, () -> {
            em.persist(ticketMessage);
            em.flush();
        });
        em.getTransaction().rollback();
    }

    @Test
    void  content2000CharsExceeded(){
        TicketMessage ticketMessage = invoice();
        ticketMessage.setContent("a".repeat(20001));

        em.getTransaction().begin();
        assertThrows(PersistenceException.class, () ->{
            em.persist(ticketMessage);
            em.flush();
        });
        em.getTransaction().rollback();
    }
    
    private TicketMessage invoice(){
        return TicketMessage
                .builder()
                .ticket(ticket)
                .sender(user)
                .content("Hi! I have an issue with cash transfer! sos! help me!")
                .build();
    }

    private void persist(TicketMessage ticketMessage){
        em.getTransaction().begin();
        if (user.getId() == null){
            em.persist(user);
        }
        if (ticket.getId() == null){
            em.persist(ticket);
        }
        em.persist(ticket);
        em.getTransaction().commit();
    }
}
