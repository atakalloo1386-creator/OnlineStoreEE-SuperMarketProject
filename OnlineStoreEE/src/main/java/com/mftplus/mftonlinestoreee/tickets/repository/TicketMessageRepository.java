package com.mftplus.mftonlinestoreee.tickets.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.tickets.entity.TicketMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class TicketMessageRepository extends BaseRepository<TicketMessage> {

    public TicketMessageRepository(){
    }

    public TicketMessageRepository(EntityManager entityManager){
        super(entityManager);
    }

    @Override
    protected Class<TicketMessage> entityType() {
        return TicketMessage.class;
    }

    public List<TicketMessage> findByTicket(Long ticketId){
        return entityManager
                .createQuery("select tm from TicketMessage tm"
                        + " where tm.ticket.id = :ticketId"
                        + " order by tm.id", TicketMessage.class)
                .setParameter("ticketId", ticketId)
                .getResultList();
    }
}
