package com.mftplus.mftonlinestoreee.users.repository;

import com.mftplus.mftonlinestoreee.core.repository.BaseRepository;
import com.mftplus.mftonlinestoreee.users.entity.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.Optional;

@ApplicationScoped
public class RoleRepository extends BaseRepository<Role> {

    public RoleRepository() {
    }

    public RoleRepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<Role> entityType() {
        return Role.class;
    }

    public Optional<Role> findByName(String name) {
        return entityManager
                .createQuery("select r from Role r"
                        + " where r.name= :name", Role.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }
}
