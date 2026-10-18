package com.mftplus.mftonlinestoreee.tickets.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.tickets.entity.Ticket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class TicketRepository extends BaseRepository<Ticket> {

    public TicketRepository() {
    }

    public TicketRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Ticket> entityType() {
        return Ticket.class;
    }

    public List<Ticket> findByUser(Long userId) {
        return entityManager
                .createQuery("select t from Ticket t"
                        + " where t.user.id = :userId"
                        + " order by t.id", Ticket.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    //  Sort by Last Updated Ticket
    public List<Ticket> findByUserLastUpdated(Long userId) {
        return entityManager
                .createQuery("select t from Ticket t"
                        + " where t.user.id = :userId"
                        + " order by t.updatedAt desc", Ticket.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
