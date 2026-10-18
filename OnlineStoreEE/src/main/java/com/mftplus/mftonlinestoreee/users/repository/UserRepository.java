package com.mftplus.mftonlinestoreee.users.repository;


import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.users.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.Optional;


@ApplicationScoped
public class UserRepository extends BaseRepository<User> {
    public UserRepository() {
    }

    public UserRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<User> entityType() {
        return User.class;
    }

    public Optional<User> findByUsername(String username) {
        return entityManager
                .createQuery( "select u from User u"
                                + " where u.username= :username" ,User.class)
                .setParameter("username",username)
                .getResultStream()
                .findFirst();
    }

    public long countByRole(Long roleId){
        return entityManager
                .createQuery("select count(u)"
                        + " from User u"
                        + " join u.roles r"
                        + " where r.id= :roleId", Long.class)
                .setParameter("roleId",roleId)
                .getSingleResult();

    }
}
