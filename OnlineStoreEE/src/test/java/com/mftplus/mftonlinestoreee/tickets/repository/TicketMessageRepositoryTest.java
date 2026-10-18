package com.mftplus.mftonlinestoreee.tickets.repository;

import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import com.mftplus.mftonlinestoreee.tickets.entity.TicketMessage;
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
import static org.junit.jupiter.api.Assertions.*;

class TicketMessageRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private TicketMessageRepository repository;

    private final User user = TestDataFactory.createUser();
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("testPU");
        em = emf.createEntityManager();
        repository = new TicketMessageRepository(em);

        TicketRepository ticketRepository = new TicketRepository(em);

        em.getTransaction().begin();
        ticket = ticketRepository.save(Ticket.builder()
                .subject("Test Ticket")
                .user(user)
                .status(OPEN)
                .build());
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    void saveAndFindById() {
        TicketMessage saved = save("Hello, this is a test message");

        Optional<TicketMessage> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Hello, this is a test message", found.get().getContent());
        assertEquals(ticket.getId(), found.get().getTicket().getId());
        assertEquals(user.getId(), found.get().getSender().getId());
    }

    @Test
    void findByTicket() {
        save("First message");
        save("Second message");
        save("Third message");

        List<TicketMessage> found = repository.findByTicket(ticket.getId());

        assertEquals(3, found.size());
        assertEquals(List.of("First message", "Second message", "Third message"),
                found.stream().map(TicketMessage::getContent).toList());
    }

    @Test
    void findByTicket_withWrongId_returnsEmpty() {
        save("Some message");

        assertTrue(repository.findByTicket(999L).isEmpty());
    }

    @Test
    void deletedRowsAreNotFound() {
        TicketMessage saved = save("Message to be deleted");

        em.getTransaction().begin();
        repository.delete(saved.getId());
        em.getTransaction().commit();

        assertTrue(repository.findById(saved.getId()).isEmpty());
        assertTrue(repository.findByTicket(ticket.getId()).isEmpty());
        assertEquals(0, repository.count());
    }

    private TicketMessage save(String content) {
        em.getTransaction().begin();
        TicketMessage saved = repository.save(TicketMessage.builder()
                .content(content)
                .ticket(ticket)
                .sender(user)
                .build());
        em.getTransaction().commit();
        return saved;
    }
}
