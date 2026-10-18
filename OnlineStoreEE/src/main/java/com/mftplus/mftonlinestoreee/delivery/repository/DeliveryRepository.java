package com.mftplus.mftonlinestoreee.delivery.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.delivery.entity.Delivery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;


@ApplicationScoped
public class DeliveryRepository extends BaseRepository<Delivery> {

    public DeliveryRepository() {

    }

    public DeliveryRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Delivery> entityType() {
        return Delivery.class;
    }

    public Optional<Delivery> findByTrackingNumber(String trackingNumber) {
        return entityManager
                .createQuery("select d from Delivery d where d.trackingNumber =:trackingNumber", Delivery.class)
                .setParameter("trackingNumber", trackingNumber)
                .getResultStream()
                .findFirst();
    }

    public List<Delivery> findByCourier(Long courierId) {
        return entityManager
                .createQuery("select d from Delivery d where d.courier.id=:courierId order by d.id", Delivery.class)
                .setParameter("courierId", courierId)
                .getResultList();
    }

    public Long countByCourier(Long courierId) {
        return entityManager
                .createQuery("select count(d) from Delivery d where d.courier.id=:courierId", Long.class)
                .setParameter("courierId", courierId)
                .getSingleResult();
    }

}
