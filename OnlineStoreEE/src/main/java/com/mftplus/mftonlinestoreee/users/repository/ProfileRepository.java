package com.mftplus.mftonlinestoreee.users.repository;


import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.users.entity.Profile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.Optional;

@ApplicationScoped
public class ProfileRepository extends BaseRepository<Profile> {
    public ProfileRepository() {
    }

    public ProfileRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Profile> entityType() {
        return Profile.class;
    }

    public Optional<Profile> findByUser(Long userId) {
        return entityManager
                .createQuery("select p from Profile p"
                        + " where p.user.id= :userId", Profile.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst();
    }


}
