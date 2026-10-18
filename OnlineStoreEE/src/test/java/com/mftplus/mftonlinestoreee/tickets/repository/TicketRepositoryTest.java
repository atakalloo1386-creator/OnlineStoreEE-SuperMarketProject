package com.mftplus.mftonlinestoreee.tickets.repository;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus;
import com.mftplus.mftonlinestoreee.tickets.entity.testutil.TestDataFactory;
import com.mftplus.mftonlinestoreee.users.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus.OPEN;
import static com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus.IN_PROGRESS;
import static com.mftplus.mftonlinestoreee.tickets.entity.enums.TicketStatus.CLOSED;
import static org.junit.jupiter.api.Assertions.*;

class TicketRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private TicketRepository repository;
    private final User user = TestDataFactory.createUser();

    @BeforeEach
    void setUp(){
        emf = Persistence.createEntityManagerFactory("testPU");
        em = emf.createEntityManager();
        repository = new TicketRepository(em);
    }

    @AfterEach
    void tearDown(){
        em.close();
        emf.close();
    }

    @Test
    void saveAndFindById(){
        Ticket saved = save("Financial Issue", user, OPEN);

        Optional<Ticket> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(user,found.get().getUser());
    }

    @Test
    void findByUser(){
        save("Financial Issue", user, OPEN);
        save("Order Issue", user, IN_PROGRESS);
        save("Ticket Issue", user, CLOSED);

        List<Ticket> found = repository.findByUser(user.getId());

        assertEquals(List.of("Financial Issue", "Order Issue", "Ticket Issue"),
                found.stream().map(Ticket::getSubject));

    }

    @Test
    void findByUserLastUpdated(){
        save("Ticket Issue", user, CLOSED);

        assertTrue(repository.findByUserLastUpdated(user.getId()).isEmpty());
        assertTrue(repository.findByUserLastUpdated(5L).isEmpty(), "another user");
    }

    @Test
    void deletedRowsAreNotFound(){
        Ticket saved = save("Ticket Issue", user, CLOSED);
        em.getTransaction().begin();
        repository.delete(saved.getId());
        em.getTransaction().commit();

        assertTrue(repository.findById(saved.getId()).isEmpty());
        assertTrue(repository.findByUser(saved.getUser().getId()).isEmpty());
        assertEquals(0, repository.count());
    }

    @Test
    void findPage(){
        save("Financial Issue", user, OPEN);
        save("Order Issue", user, IN_PROGRESS);
        save("Ticket Issue", user, CLOSED);

        PageResult<Ticket> page = repository.findPage(1,2);

        assertEquals(1, page.content().size());
        assertEquals("Ticket Issue", page.content().get(0).getSubject());
        assertEquals(3, page.totalElements());
        assertEquals(2, page.totalPages());
    }

    private Ticket save(String subject, User user, TicketStatus status){
        em.getTransaction().begin();
        Ticket saved = repository.save(Ticket.builder()
                .subject(subject)
                .user(user)
                .status(status)
                .build());
        em.getTransaction().commit();
        return saved;
    }
}
