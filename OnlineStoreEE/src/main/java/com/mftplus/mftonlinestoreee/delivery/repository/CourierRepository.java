package com.mftplus.mftonlinestoreee.delivery.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.delivery.entity.Courier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.Optional;

@ApplicationScoped
public class CourierRepository extends BaseRepository<Courier> {
    public CourierRepository() {

    }

    public CourierRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Courier> entityType() {
        return Courier.class;
    }

    public Optional<Courier> findByEmail(String email) {
        return entityManager
                .createQuery("select c from Courier c where c.email =:email", Courier.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }


}
